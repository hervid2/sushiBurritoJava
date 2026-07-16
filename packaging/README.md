# Packaging — native installers

Iteration 7 packages Sushi Burrito as a **self-contained native installer** with the JDK's
`jpackage`: the installer embeds a trimmed Java runtime (built with `jlink`), so the end user
never installs Java. The database configuration stays **outside** the binary — the installed app
reads it from environment variables at launch, so the same installer targets a local MySQL or
Railway without recompiling.

```
mvn package          ->  target/sushi-burrito.jar   (Spring Boot fat jar)
jlink                ->  target/runtime             (reduced JDK, ~65 MB)
jpackage             ->  dist/Sushi Burrito-1.0.0.msi  (Windows)  /  dist/Sushi Burrito-1.0.0.dmg (macOS)
```

`jpackage` only builds an installer for the OS it runs on, so Windows and macOS are packaged on
their own machine (or on a matching CI runner).

---

## Windows (`.msi`)

**Build it:**

```powershell
# JdkHome must be a JDK >= 24 when using the modern WiX toolset (see below).
pwsh -File scripts/package-windows.ps1 -JdkHome "C:\Program Files\Java\jdk-25"
```

Output: `dist/Sushi Burrito-1.0.0.msi` (~87 MB). Other options: `-Type app-image` (a runnable
folder, no WiX needed — a quick smoke test), `-Type exe`, `-SkipBuild` (reuse the current jar),
`-AppVersion 1.2.3`.

**Prerequisites**

- **JDK for building.** Compiling the app needs JDK **21+**. Building the `.msi` also needs
  `jpackage` from a JDK whose WiX support matches the installed WiX (see next point).
- **WiX toolset** (only for `.msi`/`.exe`, not for `app-image`). Two options:
  - **WiX 4/5** (`wix.exe`, a .NET tool) — requires **`jpackage` from JDK 24+**:
    ```powershell
    dotnet tool install --global wix --version 5.0.2
    wix extension add --global WixToolset.UI.wixext/5.0.2
    wix extension add --global WixToolset.Util.wixext/5.0.2
    ```
    The two extensions are mandatory — `jpackage` passes `-ext WixToolset.UI.wixext -ext
    WixToolset.Util.wixext`, and without them `wix.exe` fails with exit code 144.
  - **WiX 3.14** (`candle.exe`/`light.exe`) — works with any `jpackage`, but needs the .NET
    Framework 3.5 feature enabled.

  > On this project's build machine WiX 5 + JDK 25 was used, because `jpackage` in JDK 23 still
  > requires WiX 3.x and only JDK 24+ understands the single-executable WiX 4/5.

**Regenerating the app icon** (`packaging/windows/SushiBurrito.ico`) — only when the logo changes:

```powershell
pwsh -File scripts/make-windows-icon.ps1
```

---

## macOS (`.dmg`) — documented, not yet built

macOS is not available on the current build machine, so `scripts/package-macos.sh` is provided but
**untested**. On a Mac (or a `macos-latest` CI runner) with a JDK 21+ and Maven:

```bash
scripts/package-macos.sh -j "$JAVA_HOME"          # -> dist/Sushi Burrito-1.0.0.dmg
scripts/package-macos.sh -j "$JAVA_HOME" -t pkg   # a .pkg instead
```

The macOS icon (`.icns`) is generated on the fly from the same logo with the built-in `sips` and
`iconutil`, so nothing macOS-specific is committed.

---

## After installing — database configuration

The app reads the database from the environment (never from inside the installer). Set these before
the first launch:

| Variable | Example | Notes |
|---|---|---|
| `DB_URL` | `jdbc:mysql://<host>:<port>/sushiburrito_db` | JDBC form, no user/password embedded |
| `DB_USERNAME` | `root` | no fallback — must be set |
| `DB_PASSWORD` | *(secret)* | no fallback — must be set |
| `SPRING_PROFILES_ACTIVE` | `railway` | only when targeting Railway |

On Windows, set them as **user environment variables** (Settings → *Edit environment variables for
your account*) so the Start-menu shortcut inherits them. If the variables are missing the app fails
to connect on startup, as designed.
