<#
.SYNOPSIS
    Builds the self-contained Windows installer (or app-image) for Sushi Burrito with jpackage.

.DESCRIPTION
    Produces an installer that bundles a trimmed Java runtime, so the end user never installs Java.
    The pipeline is:

        1. mvn package          -> the Spring Boot fat jar (target/sushi-burrito.jar)
        2. jlink                -> a reduced JDK runtime holding only the modules the app needs
        3. jpackage             -> an .msi (default), .exe or app-image, with the runtime embedded

    The database configuration stays OUTSIDE the binary: the installed app reads DB_URL, DB_USERNAME,
    DB_PASSWORD (and optionally SPRING_PROFILES_ACTIVE=railway) from the environment at launch, so the
    same installer works against a local MySQL or Railway without recompiling.

.PARAMETER JdkHome
    JDK used for the build/jlink/jpackage steps. Must be >= 21 to compile the app. When the modern WiX
    toolset (wix.exe, v4/v5) is used to build the .msi, jpackage must come from JDK 24+; older WiX 3.x
    (candle.exe/light.exe) works with any jpackage. Defaults to $env:JAVA_HOME.

.PARAMETER Type
    jpackage installer type: 'msi' (default), 'exe' or 'app-image' (a runnable folder, no installer,
    needs no WiX -- handy for a quick smoke test).

.PARAMETER AppVersion
    Installer version (MAJOR.MINOR.PATCH). Drives the MSI ProductVersion. Defaults to 1.0.0.

.PARAMETER SkipBuild
    Reuse an existing target/sushi-burrito.jar instead of running Maven again.

.EXAMPLE
    pwsh -File scripts/package-windows.ps1 -JdkHome "C:\Program Files\Java\jdk-25"

.EXAMPLE
    pwsh -File scripts/package-windows.ps1 -Type app-image -SkipBuild
#>
[CmdletBinding()]
param(
    [string]$JdkHome = $env:JAVA_HOME,
    [ValidateSet('msi', 'exe', 'app-image')]
    [string]$Type = 'msi',
    [string]$AppVersion = '1.0.0',
    [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRoot

$appName = 'Sushi Burrito'
$vendor = 'Sushi Burrito'
$description = 'Restaurant management desktop application'
$finalJarName = 'sushi-burrito.jar'
$iconPath = Join-Path $repositoryRoot 'packaging/windows/SushiBurrito.ico'

# The jlink module set: the base list jdeps reports for our own classes, plus the modules that Spring,
# Hibernate, the MySQL driver and PDFBox reach only through reflection or the ServiceLoader (which
# jdeps cannot see): JNDI, JGSS/SASL and the TLS crypto providers (needed for the encrypted Railway
# connection), the XML stack, JUL logging, extra charsets and the jar filesystem. Verified complete by
# booting the fat jar on the trimmed runtime: the Spring context initialises fully and only stops at
# the actual database connection.
$runtimeModules = @(
    'java.base', 'java.compiler', 'java.desktop', 'java.instrument', 'java.management',
    'java.naming', 'java.net.http', 'java.prefs', 'java.rmi', 'java.scripting',
    'java.security.jgss', 'java.security.sasl', 'java.sql', 'java.sql.rowset',
    'java.xml', 'java.xml.crypto', 'java.logging',
    'jdk.jfr', 'jdk.unsupported', 'jdk.crypto.ec', 'jdk.crypto.cryptoki',
    'jdk.charsets', 'jdk.zipfs', 'jdk.localedata'
) -join ','

function Resolve-JdkTool {
    param([string]$Tool)
    if (-not $JdkHome) {
        throw "JAVA_HOME is not set and -JdkHome was not passed. Point it at a JDK >= 21 (JDK 24+ to build the .msi with modern WiX)."
    }
    $path = Join-Path $JdkHome "bin/$Tool.exe"
    if (-not (Test-Path $path)) {
        throw "$Tool.exe not found under '$JdkHome'. Is -JdkHome a valid JDK?"
    }
    return $path
}

$javaExe = Resolve-JdkTool 'java'
$jlinkExe = Resolve-JdkTool 'jlink'
$jpackageExe = Resolve-JdkTool 'jpackage'

# Feature version, e.g. "25" from "25.0.3". Used only to check the WiX/jpackage compatibility.
# `java -version` prints to stderr, which -ErrorAction Stop would otherwise treat as a fatal error.
$previousErrorAction = $ErrorActionPreference
$ErrorActionPreference = 'Continue'
$javaVersionText = (& $javaExe -version 2>&1 | Out-String)
$ErrorActionPreference = $previousErrorAction
if ($javaVersionText -notmatch 'version "(\d+)') { throw "Could not parse the Java version from '$javaExe'." }
$javaFeature = [int]$Matches[1]

if ($Type -ne 'app-image') {
    $wixV4Plus = Get-Command 'wix.exe' -ErrorAction SilentlyContinue
    $wixV3 = Get-Command 'light.exe' -ErrorAction SilentlyContinue
    if (-not $wixV4Plus -and -not $wixV3) {
        throw "WiX is required to build a $Type installer but was not found on PATH. Install WiX 4/5 (dotnet tool install --global wix) and add its UI/Util extensions, or WiX 3.14."
    }
    if ($wixV4Plus -and -not $wixV3 -and $javaFeature -lt 24) {
        throw "The modern WiX toolset (wix.exe) needs jpackage from JDK 24+, but -JdkHome is JDK $javaFeature. Use a JDK 24+ or install WiX 3.14."
    }
}

Write-Host "== Sushi Burrito Windows packaging ==" -ForegroundColor Cyan
Write-Host "JDK           : $JdkHome (feature $javaFeature)"
Write-Host "Type          : $Type"
Write-Host "App version   : $AppVersion"

# --- 1. Fat jar -------------------------------------------------------------------------------------
if (-not $SkipBuild) {
    Write-Host "`n[1/3] Building the fat jar with Maven..." -ForegroundColor Cyan
    $mvn = Get-Command 'mvn.cmd', 'mvn' -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $mvn) { throw "Maven (mvn) was not found on PATH." }
    # Maven honours JAVA_HOME; point it at the packaging JDK so the app compiles at its Java 21 target.
    $env:JAVA_HOME = $JdkHome
    & $mvn.Source -q clean package -DskipTests
    if ($LASTEXITCODE -ne 0) { throw "Maven build failed." }
} else {
    Write-Host "`n[1/3] Skipping Maven build (-SkipBuild)." -ForegroundColor Yellow
}
$fatJar = Join-Path $repositoryRoot "target/$finalJarName"
if (-not (Test-Path $fatJar)) { throw "Fat jar not found at $fatJar. Run without -SkipBuild first." }

# --- 2. Trimmed runtime -----------------------------------------------------------------------------
Write-Host "`n[2/3] Building the trimmed runtime with jlink..." -ForegroundColor Cyan
$runtimeDir = Join-Path $repositoryRoot 'target/runtime'
if (Test-Path $runtimeDir) { Remove-Item -Recurse -Force $runtimeDir }
& $jlinkExe --no-header-files --no-man-pages --strip-debug --compress zip-6 `
    --add-modules $runtimeModules --output $runtimeDir
if ($LASTEXITCODE -ne 0) { throw "jlink failed." }

# --- 3. Installer -----------------------------------------------------------------------------------
Write-Host "`n[3/3] Building the $Type with jpackage..." -ForegroundColor Cyan
# jpackage reads the Main-Class (Spring Boot's JarLauncher) from the fat jar manifest, so --input holds
# ONLY that jar.
$inputDir = Join-Path $repositoryRoot 'target/app-input'
if (Test-Path $inputDir) { Remove-Item -Recurse -Force $inputDir }
New-Item -ItemType Directory -Force -Path $inputDir | Out-Null
Copy-Item $fatJar $inputDir

$destDir = if ($Type -eq 'app-image') { Join-Path $repositoryRoot 'target/jpackage-out' } else { Join-Path $repositoryRoot 'dist' }
if (Test-Path $destDir) { Remove-Item -Recurse -Force $destDir }
New-Item -ItemType Directory -Force -Path $destDir | Out-Null

$jpackageArgs = @(
    '--type', $Type,
    '--name', $appName,
    '--app-version', $AppVersion,
    '--vendor', $vendor,
    '--description', $description,
    '--icon', $iconPath,
    '--input', $inputDir,
    '--main-jar', $finalJarName,
    '--runtime-image', $runtimeDir,
    '--dest', $destDir
)
if ($Type -ne 'app-image') {
    # Program Files install with a desktop shortcut, a Start-menu entry and a "choose folder" page.
    $jpackageArgs += @('--win-shortcut', '--win-menu', '--win-menu-group', $appName, '--win-dir-chooser')
}

& $jpackageExe @jpackageArgs
if ($LASTEXITCODE -ne 0) { throw "jpackage failed." }

Write-Host "`nDone. Output in: $destDir" -ForegroundColor Green
Get-ChildItem -Recurse $destDir | Where-Object { $_.Extension -in '.msi', '.exe' -and $_.Name -like 'Sushi*' } |
    ForEach-Object { Write-Host ("  {0}  ({1} MB)" -f $_.Name, [Math]::Round($_.Length / 1MB, 1)) }

Write-Host "`nReminder: before first launch set DB_URL / DB_USERNAME / DB_PASSWORD (and" -ForegroundColor Yellow
Write-Host "SPRING_PROFILES_ACTIVE=railway for Railway) as environment variables." -ForegroundColor Yellow
