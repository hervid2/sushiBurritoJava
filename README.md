# Sushi Burrito

Aplicación de escritorio (Java Swing) para la gestión de un restaurante.

> Proyecto en refactorización de Swing nativo hacia Spring Boot. Ver el plan en `roadmap.md`
> y las buenas prácticas en `skills.md`. Este README se ampliará (en inglés) en la iteración final.

## Requisitos

- **JDK 21** o superior (el proyecto compila con `--release 21`).
- **Maven 3.9+**. Si Maven usa por defecto un JDK anterior a 21, apunta `JAVA_HOME` a un
  JDK ≥ 21 antes de compilar, por ejemplo:

  ```bash
  # Windows (Git Bash)
  export JAVA_HOME="/c/Program Files/Java/jdk-23"
  ```
- **MySQL** en ejecución con el esquema de la aplicación (la persistencia por JDBC se migra en
  iteraciones posteriores del roadmap).

## Compilar

```bash
mvn clean package
```

Genera un JAR ejecutable con todas las dependencias embebidas en `target/sushi-burrito.jar`.

## Ejecutar

```bash
java -jar target/sushi-burrito.jar
```

Se abre la ventana de Login, punto de entrada de la aplicación.

## Estructura

```
src/main/java/com/restaurante/app
  ├─ controllers/   Orquestación entre vistas y datos
  ├─ database/      Acceso a datos (JDBC, temporal)
  ├─ models/        Modelos de dominio y DTOs
  └─ views/         Interfaz Swing (authentication, admin, mesero, cocina)
src/main/resources/images   Recursos gráficos de la UI
lib/                        Dependencias locales de referencia
```
