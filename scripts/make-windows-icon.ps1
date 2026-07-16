<#
.SYNOPSIS
    Regenerates the Windows application icon (packaging/windows/SushiBurrito.ico) from the app logo.

.DESCRIPTION
    jpackage needs a square, multi-resolution .ico to brand the launcher, the Start menu entry and the
    installer. The source logo is a 592x422 JPEG, so it is letterboxed onto a square canvas (padding with
    the logo's own background colour) instead of being cropped, which would cut off the wordmark.

    The icon is committed to the repository, so this script only needs to be re-run when the logo changes.

.EXAMPLE
    pwsh -File scripts/make-windows-icon.ps1
#>
[CmdletBinding()]
param(
    [string]$SourceImage,
    [string]$OutputIcon
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

# $PSScriptRoot is not bound yet while parameter defaults are evaluated, so the repository-relative
# defaults are resolved here instead.
$repositoryRoot = Split-Path -Parent $PSScriptRoot
if (-not $SourceImage) { $SourceImage = Join-Path $repositoryRoot 'src/main/resources/images/icons/logo.jpg' }
if (-not $OutputIcon)  { $OutputIcon  = Join-Path $repositoryRoot 'packaging/windows/SushiBurrito.ico' }

# Windows picks the closest entry for each context (16 = title bar, 32 = taskbar, 256 = large tiles).
# Entries at or above this size are stored PNG-compressed (which keeps the file small) and the smaller
# ones as uncompressed DIBs: that is the layout every Windows icon reader handles, including the legacy
# GDI+ decoder that cannot read PNG-compressed entries at all.
$iconSizes = @(256, 128, 64, 48, 32, 16)
$smallestPngCompressedSize = 128

# Converts a bitmap into the ICO flavour of a DIB: a BITMAPINFOHEADER whose height covers both the
# colour bitmap and the (unused, alpha supersedes it) AND mask, followed by bottom-up BGRA rows.
function ConvertTo-IconDib {
    param([System.Drawing.Bitmap]$Bitmap)

    $size = $Bitmap.Width
    $bounds = New-Object System.Drawing.Rectangle(0, 0, $size, $size)
    $locked = $Bitmap.LockBits($bounds, [System.Drawing.Imaging.ImageLockMode]::ReadOnly, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        $pixels = New-Object byte[] ($locked.Stride * $size)
        [System.Runtime.InteropServices.Marshal]::Copy($locked.Scan0, $pixels, 0, $pixels.Length)
    } finally {
        $Bitmap.UnlockBits($locked)
    }

    $dib = New-Object System.IO.MemoryStream
    $dibWriter = New-Object System.IO.BinaryWriter($dib)
    try {
        $dibWriter.Write([uint32]40)          # BITMAPINFOHEADER size
        $dibWriter.Write([int32]$size)        # width
        $dibWriter.Write([int32]($size * 2))  # height: colour bitmap + AND mask
        $dibWriter.Write([uint16]1)           # planes
        $dibWriter.Write([uint16]32)          # bits per pixel
        $dibWriter.Write([uint32]0)           # BI_RGB (uncompressed)
        $dibWriter.Write([uint32]0)           # image size (may be 0 for BI_RGB)
        $dibWriter.Write([int32]0)            # horizontal resolution
        $dibWriter.Write([int32]0)            # vertical resolution
        $dibWriter.Write([uint32]0)           # palette colours used
        $dibWriter.Write([uint32]0)           # important palette colours

        # DIB rows run bottom-up, the opposite of the top-down buffer LockBits handed back.
        for ($row = $size - 1; $row -ge 0; $row--) {
            $dibWriter.Write($pixels, $row * $locked.Stride, $size * 4)
        }

        # AND mask: 1 bit per pixel, rows padded to 4 bytes. Left all-zero (fully opaque) because
        # 32-bit entries are composited from the alpha channel instead.
        $maskStride = [Math]::Floor((($size + 31) / 32)) * 4
        $dibWriter.Write((New-Object byte[] ($maskStride * $size)))
        $dibWriter.Flush()
        # The leading comma stops PowerShell from unrolling the array into the pipeline, which would
        # hand the caller an Object[] and silently pick the wrong BinaryWriter.Write overload later.
        return ,$dib.ToArray()
    } finally {
        $dibWriter.Dispose()
        $dib.Dispose()
    }
}

$sourcePath = (Resolve-Path $SourceImage).Path
$logo = [System.Drawing.Image]::FromFile($sourcePath)

try {
    # Pad to a square using the logo's own background so the padding is invisible.
    $canvasSize = [Math]::Max($logo.Width, $logo.Height)
    $square = New-Object System.Drawing.Bitmap($canvasSize, $canvasSize)
    $graphics = [System.Drawing.Graphics]::FromImage($square)
    try {
        $backgroundColor = ([System.Drawing.Bitmap]$logo).GetPixel(0, 0)
        $graphics.Clear($backgroundColor)
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.DrawImage($logo, [int](($canvasSize - $logo.Width) / 2), [int](($canvasSize - $logo.Height) / 2), $logo.Width, $logo.Height)
    } finally {
        $graphics.Dispose()
    }

    # Render every size up front; the ICO container is assembled by hand below.
    $renderedIcons = New-Object System.Collections.Generic.List[object]
    foreach ($size in $iconSizes) {
        $scaled = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $scaledGraphics = [System.Drawing.Graphics]::FromImage($scaled)
        try {
            $scaledGraphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
            $scaledGraphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
            $scaledGraphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
            $scaledGraphics.DrawImage($square, 0, 0, $size, $size)
        } finally {
            $scaledGraphics.Dispose()
        }

        if ($size -ge $smallestPngCompressedSize) {
            $buffer = New-Object System.IO.MemoryStream
            $scaled.Save($buffer, [System.Drawing.Imaging.ImageFormat]::Png)
            [byte[]]$payload = $buffer.ToArray()
            $buffer.Dispose()
        } else {
            [byte[]]$payload = ConvertTo-IconDib -Bitmap $scaled
        }
        $renderedIcons.Add([pscustomobject]@{ Size = $size; Data = $payload })
        $scaled.Dispose()
    }
    $square.Dispose()

    # ICO container: 6-byte ICONDIR, then one 16-byte ICONDIRENTRY per image, then the payloads.
    $outputPath = [System.IO.Path]::GetFullPath($OutputIcon)
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputPath) | Out-Null

    $stream = [System.IO.File]::Create($outputPath)
    $writer = New-Object System.IO.BinaryWriter($stream)
    try {
        $writer.Write([uint16]0)                       # reserved
        $writer.Write([uint16]1)                       # type: 1 = icon
        $writer.Write([uint16]$renderedIcons.Count)    # image count

        # Payloads start right after the directory; 256px is stored as 0 in the single-byte dimensions.
        $payloadOffset = 6 + (16 * $renderedIcons.Count)
        foreach ($icon in $renderedIcons) {
            $writer.Write([byte]($icon.Size % 256))    # width  (0 means 256)
            $writer.Write([byte]($icon.Size % 256))    # height (0 means 256)
            $writer.Write([byte]0)                     # palette size: 0 = truecolour
            $writer.Write([byte]0)                     # reserved
            $writer.Write([uint16]1)                   # colour planes
            $writer.Write([uint16]32)                  # bits per pixel
            $writer.Write([uint32]$icon.Data.Length)
            $writer.Write([uint32]$payloadOffset)
            $payloadOffset += $icon.Data.Length
        }
        foreach ($icon in $renderedIcons) {
            $writer.Write([byte[]]$icon.Data)
        }
    } finally {
        $writer.Dispose()
        $stream.Dispose()
    }

    Write-Host "Icon written to $outputPath ($([Math]::Round((Get-Item $outputPath).Length / 1KB, 1)) KB, sizes: $($iconSizes -join ', '))"
} finally {
    $logo.Dispose()
}
