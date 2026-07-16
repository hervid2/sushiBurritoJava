<#
.SYNOPSIS
    Preflight, build and launch of Sushi Burrito for the Iteration 6 manual verification
    (BCrypt hashing + externalized credentials).

.DESCRIPTION
    Checks the JDK and the database credentials, builds the executable jar, prints the
    step-by-step verification checklist and launches the application.

    Credentials are NEVER printed or written anywhere: they are read from the environment
    or from the gitignored application-local.properties.

.EXAMPLE
    .\scripts\verify-login.ps1
    .\scripts\verify-login.ps1 -SkipBuild
    .\scripts\verify-login.ps1 -SpringProfile local
#>
param(
    [switch]$SkipBuild,
    [string]$SpringProfile = "railway"
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

function Write-Section($text) {
    Write-Host ""
    Write-Host "=== $text ===" -ForegroundColor Cyan
}

# ---------------------------------------------------------------- 1. JDK
Write-Section "1/4  JDK"

# The build targets Java 21; the machine default (JDK 17) fails with
# "release version 21 not supported".
$preferredJdk = "C:\Program Files\Java\jdk-23"
if (Test-Path $preferredJdk) {
    $env:JAVA_HOME = $preferredJdk
    Write-Host "JAVA_HOME -> $preferredJdk" -ForegroundColor Green
} else {
    Write-Host "JDK 23 no encontrado en $preferredJdk; se usa el JAVA_HOME actual:" -ForegroundColor Yellow
    Write-Host "  $env:JAVA_HOME"
    Write-Host "  Si el build falla con 'release version 21 not supported', apunta JAVA_HOME a un JDK >= 21."
}
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# ---------------------------------------------------------------- 2. Credenciales
Write-Section "2/4  Credenciales"

$localProps = Join-Path $repoRoot "application-local.properties"
$hasLocalFile = Test-Path $localProps
$hasEnvVars = $env:DB_URL -and $env:DB_USERNAME -and $env:DB_PASSWORD

if (-not $hasEnvVars -and -not $hasLocalFile) {
    Write-Host "No hay credenciales de base de datos." -ForegroundColor Red
    Write-Host ""
    Write-Host "Opcion A - archivo local (gitignored):"
    Write-Host "    copy application-local.properties.example application-local.properties"
    Write-Host "    # y rellena DB_URL / DB_USERNAME / DB_PASSWORD"
    Write-Host ""
    Write-Host "Opcion B - variables de entorno solo para esta sesion:"
    Write-Host '    $env:DB_URL      = "jdbc:mysql://<host>.proxy.rlwy.net:<puerto>/sushiburrito_db"'
    Write-Host '    $env:DB_USERNAME = "root"'
    Write-Host '    $env:DB_PASSWORD = "<password>"'
    Write-Host "    (la contrasena queda en el historial de la consola: preferible la Opcion A)"
    Write-Host ""
    Write-Host "Recuerda: la contrasena de Railway quedo expuesta en una captura durante la"
    Write-Host "depuracion de la Iteracion 5. Rotala antes de usarla:"
    Write-Host "    ALTER USER 'root'@'%' IDENTIFIED BY '<nueva>'; FLUSH PRIVILEGES;"
    Write-Host "    # y actualiza MYSQL_ROOT_PASSWORD en Railway para que coincidan"
    exit 1
}

# Se muestra el destino, nunca el secreto.
if ($hasEnvVars) {
    Write-Host "Origen: variables de entorno" -ForegroundColor Green
    Write-Host "  DB_URL      = $env:DB_URL"
    Write-Host "  DB_USERNAME = $env:DB_USERNAME"
    Write-Host "  DB_PASSWORD = (definida, no se imprime)"
} else {
    Write-Host "Origen: application-local.properties (gitignored)" -ForegroundColor Green
    Write-Host "  Lo carga Spring via spring.config.import; por eso hay que ejecutar desde $repoRoot"
}
Write-Host "Perfil activo: $SpringProfile"

# ---------------------------------------------------------------- 3. Build
Write-Section "3/4  Build"

$jar = Join-Path $repoRoot "target\sushi-burrito.jar"
if ($SkipBuild -and (Test-Path $jar)) {
    Write-Host "-SkipBuild: se reutiliza el jar existente." -ForegroundColor Yellow
} else {
    mvn -q clean package "-DskipTests"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "El build fallo. No se lanza la app." -ForegroundColor Red
        exit 1
    }
    Write-Host "Build OK -> target\sushi-burrito.jar" -ForegroundColor Green
}

# ---------------------------------------------------------------- 4. Checklist
Write-Section "4/4  Checklist de verificacion manual"

$checklist = @'

Usuarios de demo (V2 + V3):  admin@sushiburrito.com / admin123
                             waiter@sushiburrito.com / waiter123
                             cook@sushiburrito.com  / cook123

--- EN EL ARRANQUE (mira los logs de esta consola) ---

[ ] 0. Flyway aplica la migracion nueva sobre la base ya existente. Busca una linea:
       "Migrating schema `sushiburrito_db` to version 3 - rehash demo passwords with bcrypt"
       En arranques posteriores dira "Successfully validated 3 migrations" y no la repetira.
       Si aparece "Validate failed ... checksum mismatch" -> se edito una migracion ya
       aplicada (V1/V2); no debe pasar, no las toques.

--- LOGIN (lo que cambia esta iteracion) ---

[ ] 1. BCrypt de extremo a extremo: entra con admin@sushiburrito.com / admin123.
       Es la prueba de que el hash de V3 lo genero el mismo encoder que valida el login.
       Debe abrir el panel de administrador igual que antes.

[ ] 2. Contrasena incorrecta: admin@sushiburrito.com / admin124 -> "Contrasena incorrecta."
       Correo inexistente: nadie@x.com -> "Correo no registrado."
       (Los mensajes son los de siempre: la UI no cambia.)

[ ] 3. Usuario nuevo se guarda hasheado. Como admin: Gestion de Usuarios -> crea uno con
       contrasena fuerte (p. ej. Sushi2025!). Luego, en la BD:
           SELECT email, LEFT(password, 8) AS scheme FROM users;
       -> la fila nueva debe empezar por "{bcrypt}", nunca texto plano.
       Cierra sesion y entra con ese usuario nuevo.

[ ] 4. La politica de contrasena ahora tambien aplica al reset (antes solo rechazaba vacio):
       Login -> "Olvide mi contrasena" -> correo de un usuario -> nueva contrasena "admin123"
       -> DEBE rechazarla ("La contrasena debe tener al menos 8 caracteres...").
       Repite con "Sushi2025!" -> aceptada, y entra con ella.

[ ] 5. Re-hash transparente de una cuenta SHA-256 antigua (la ruta de migracion).
       a) Con la app cerrada, degrada a mano una cuenta al hash viejo (SHA-256 de "admin123"):
          UPDATE users
             SET password = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9'
           WHERE email = 'admin@sushiburrito.com';
       b) Lanza la app y entra con admin@sushiburrito.com / admin123 -> debe funcionar.
       c) Sin tocar nada mas, consulta:
          SELECT LEFT(password, 8) FROM users WHERE email = 'admin@sushiburrito.com';
          -> ahora debe decir "{bcrypt}": el login lo re-hasheo solo.
       d) Vuelve a entrar con admin123 -> sigue funcionando ya con el hash nuevo.

--- REGRESION (el flujo de la demo no debe haberse movido) ---

[ ] 6. mesero: crear pedido -> cocina: marcar preparado -> mesero: generar factura (PDF)
       -> admin: estadisticas. Nada de esto toca el hashing, pero confirma que el DoD global
       de la iteracion se mantiene.

--- SIN BASE DE DATOS (opcional, prueba de que no hay credenciales quemadas) ---

[ ] 7. Abre otra consola SIN las variables DB_* y sin application-local.properties y lanza
       el jar: debe fallar al conectar, nunca entrar con una cuenta por defecto.
       Antes de esta iteracion arrancaba con sushiBurrito/SBDataBaseKey2025 quemadas en el repo.

'@

Write-Host $checklist

Write-Host "Pulsa ENTER para lanzar la app (Ctrl+C aborta)..." -ForegroundColor Cyan
[void](Read-Host)

Write-Section "Lanzando (Ctrl+C para cerrar)"
$env:SPRING_PROFILES_ACTIVE = $SpringProfile
& java -jar $jar
