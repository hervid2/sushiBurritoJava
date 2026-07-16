#!/usr/bin/env bash
#
# Builds the self-contained macOS installer (.dmg) for Sushi Burrito with jpackage.
#
# Mirrors scripts/package-windows.ps1: a trimmed Java runtime is embedded so the end user never
# installs Java. The database configuration stays OUTSIDE the app -- the launcher reads DB_URL,
# DB_USERNAME, DB_PASSWORD (and optionally SPRING_PROFILES_ACTIVE=railway) from the environment.
#
# jpackage only builds an installer for the OS it runs on, so this must run on macOS. It has NOT been
# tested on macOS (the project is developed on Windows); it is provided so the .dmg is a single command
# away on a Mac or a macOS CI runner.
#
# Requirements: a JDK >= 21 (JAVA_HOME or -j), macOS with the built-in sips + iconutil tools, and Maven.
#
# Usage:
#   scripts/package-macos.sh                 # build jar + runtime + dmg
#   scripts/package-macos.sh -s              # reuse target/sushi-burrito.jar (skip Maven)
#   scripts/package-macos.sh -j /path/to/jdk -v 1.0.0 -t pkg
set -euo pipefail

APP_NAME="Sushi Burrito"
VENDOR="Sushi Burrito"
DESCRIPTION="Restaurant management desktop application"
FINAL_JAR="sushi-burrito.jar"

JDK_HOME="${JAVA_HOME:-}"
TYPE="dmg"
APP_VERSION="1.0.0"
SKIP_BUILD=0

while getopts "j:t:v:sh" opt; do
    case "$opt" in
        j) JDK_HOME="$OPTARG" ;;
        t) TYPE="$OPTARG" ;;          # dmg (default) or pkg
        v) APP_VERSION="$OPTARG" ;;
        s) SKIP_BUILD=1 ;;
        h) grep '^#' "$0" | sed 's/^# \{0,1\}//'; exit 0 ;;
        *) echo "Unknown option. Use -h for help." >&2; exit 1 ;;
    esac
done

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

if [[ -z "$JDK_HOME" ]]; then
    echo "ERROR: set JAVA_HOME or pass -j <jdk-home> (JDK >= 21)." >&2
    exit 1
fi
JLINK="$JDK_HOME/bin/jlink"
JPACKAGE="$JDK_HOME/bin/jpackage"

# Same module set as the Windows build; see scripts/package-windows.ps1 for the rationale (reflective
# and ServiceLoader-only modules jdeps cannot see, plus the TLS crypto providers for Railway).
RUNTIME_MODULES="java.base,java.compiler,java.desktop,java.instrument,java.management,\
java.naming,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,\
java.sql,java.sql.rowset,java.xml,java.xml.crypto,java.logging,jdk.jfr,jdk.unsupported,\
jdk.crypto.ec,jdk.crypto.cryptoki,jdk.charsets,jdk.zipfs,jdk.localedata"

echo "== Sushi Burrito macOS packaging =="
echo "JDK         : $JDK_HOME"
echo "Type        : $TYPE"
echo "App version : $APP_VERSION"

# --- 1. Fat jar -------------------------------------------------------------------------------------
if [[ "$SKIP_BUILD" -eq 0 ]]; then
    echo -e "\n[1/4] Building the fat jar with Maven..."
    JAVA_HOME="$JDK_HOME" mvn -q clean package -DskipTests
else
    echo -e "\n[1/4] Skipping Maven build (-s)."
fi
FAT_JAR="target/$FINAL_JAR"
[[ -f "$FAT_JAR" ]] || { echo "ERROR: $FAT_JAR not found. Run without -s first." >&2; exit 1; }

# --- 2. App icon (.icns from the logo) --------------------------------------------------------------
# jpackage wants a .icns on macOS. It is generated here from the logo with the built-in sips/iconutil
# so nothing binary is committed for macOS. The logo is letterboxed onto a square to keep the wordmark.
echo -e "\n[2/4] Building the .icns app icon..."
LOGO="src/main/resources/images/icons/logo.jpg"
ICONSET="$(mktemp -d)/SushiBurrito.iconset"
ICNS="target/SushiBurrito.icns"
mkdir -p "$ICONSET"
SIDE="$(sips -g pixelWidth -g pixelHeight "$LOGO" | awk '/pixel/ {print $2}' | sort -rn | head -1)"
SQUARE="$(mktemp).png"
# Pad (not crop) to a centred square canvas, filling with the logo's own orange background (E6441D)
# so the padding is invisible instead of the default black bars.
sips -p "$SIDE" "$SIDE" --padColor E6441D "$LOGO" --out "$SQUARE" >/dev/null
for size in 16 32 64 128 256 512; do
    sips -z "$size" "$size" "$SQUARE" --out "$ICONSET/icon_${size}x${size}.png" >/dev/null
    double=$((size * 2))
    sips -z "$double" "$double" "$SQUARE" --out "$ICONSET/icon_${size}x${size}@2x.png" >/dev/null
done
iconutil -c icns "$ICONSET" -o "$ICNS"

# --- 3. Trimmed runtime -----------------------------------------------------------------------------
echo -e "\n[3/4] Building the trimmed runtime with jlink..."
rm -rf target/runtime
"$JLINK" --no-header-files --no-man-pages --strip-debug --compress zip-6 \
    --add-modules "$RUNTIME_MODULES" --output target/runtime

# --- 4. Installer -----------------------------------------------------------------------------------
echo -e "\n[4/4] Building the $TYPE with jpackage..."
rm -rf target/app-input dist
mkdir -p target/app-input dist
cp "$FAT_JAR" target/app-input/
"$JPACKAGE" \
    --type "$TYPE" \
    --name "$APP_NAME" \
    --app-version "$APP_VERSION" \
    --vendor "$VENDOR" \
    --description "$DESCRIPTION" \
    --icon "$ICNS" \
    --input target/app-input \
    --main-jar "$FINAL_JAR" \
    --runtime-image target/runtime \
    --dest dist

echo -e "\nDone. Output in: $REPO_ROOT/dist"
ls -lh dist
echo -e "\nReminder: before first launch set DB_URL / DB_USERNAME / DB_PASSWORD (and"
echo "SPRING_PROFILES_ACTIVE=railway for Railway) as environment variables."
