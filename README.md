# PersonAPP - Laboratorio 2: Servicio Web en Arquitectura Hexagonal

Aplicación multi-módulo con **Arquitectura Hexagonal (Clean Architecture)** que expone el CRUD del modelo `Persona`, `Profesion`, `Telefono` y `Estudios` sobre **dos motores de persistencia (MariaDB y MongoDB)** y con **dos adaptadores de entrada (REST y CLI)**.

Pontificia Universidad Javeriana - Arquitectura de Software.

## Integrantes 
- Alexander Aponte Largacha
- Tomas Alejandro Silva
- Juan Pabon Vargas


## Inicio rápido (cómo se prende)

Requisitos mínimos: **Docker Desktop abierto** (motor en *Engine running*), **JDK 25** y **Maven** (solo para la opción con Swagger local). Todos los comandos se ejecutan desde la raíz del repositorio.

### Opción 1: CLI (menú de consola) desde la terminal

```bash
docker compose run --build --rm cli
```

- Construye la imagen, levanta MariaDB y MongoDB automáticamente (con sus scripts DDL/DML la primera vez) y abre el menú interactivo.
- `--build` recompila la imagen para que tome los últimos cambios del código.
- `--rm` borra el contenedor de la CLI al salir (los datos de las bases se conservan en volúmenes).
- Para salir: opción `0` en los menús.

### Opción 2: API REST con Swagger

```bash
docker compose up -d --build
```

Cuando la consola muestre `Started PersonAppRestApi`, abrir **http://localhost:3000/swagger-ui.html**. Para detenerla: `Ctrl+C`.

> Alternativa sin Maven ni JDK locales: `docker compose up -d --build` levanta las bases **y** la API REST dentro de Docker (misma URL de Swagger).

### Apagar y limpiar

```bash
docker compose down        # detiene los contenedores (conserva los datos)
docker compose down -v     # además borra los datos; la próxima vez se vuelven a cargar los scripts
```

### Si algo no arranca

| Síntoma | Solución |
|---|---|
| Docker Desktop dice **"WSL needs updating"** | PowerShell como administrador: `wsl --update`, reiniciar Docker Desktop y *Try Again* |
| `Cannot connect to the Docker daemon` | Abrir Docker Desktop y esperar a *Engine running* |
| Puerto `3307`, `27017` o `3000` ocupado | Cerrar la instalación local de MariaDB/MongoDB o la otra API que lo use |
| Cambié los scripts SQL/JS y no se reflejan | `docker compose down -v` y volver a levantar (los scripts solo corren con volúmenes vacíos) |
| La API arranca pero responde error de conexión | Verificar que las bases estén sanas: `docker compose ps` |

## Stack

| Tecnología | Versión |
|---|---|
| Java (JDK) | 25 |
| Spring Boot | 4.1.2 |
| Maven | 3.9+ |
| Lombok | 1.18.46 |
| springdoc-openapi (Swagger / OpenAPI 3) | 3.1.0 |
| MariaDB | 12.3.3 (imagen Docker) |
| MongoDB | 9.0.4 (imagen Docker) |

> **Nota:** el enunciado del laboratorio menciona JDK 11. Este proyecto se migró a **JDK 25 y Spring Boot 4** (`javax.persistence` pasó a `jakarta.persistence`, starter `spring-boot-starter-webmvc`, propiedades `spring.mongodb.*`). Con un JDK inferior no compila.

## Arquitectura

```
common                  anotaciones (@Port, @Adapter, @UseCase, @Mapper), excepciones, DatabaseOption
domain                  entidades de dominio puras: Person, Phone, Profession, Study, Gender
application             puertos de entrada (*InputPort), puertos de salida (*OutputPort) y casos de uso (*UseCase)
maria-output-adapter    adaptador de salida: JPA/Hibernate + MariaDB
mongo-output-adapter    adaptador de salida: Spring Data MongoDB
rest-input-adapter      adaptador de entrada: API REST + Swagger (puerto 3000)
cli-input-adapter       adaptador de entrada: menú de consola
```

- Los adaptadores de entrada solo conocen los **puertos de entrada** y hablan en lenguaje de dominio.
- La base de datos (`MARIA` o `MONGO`) se elige **en cada petición** (REST) o desde el menú (CLI) y se inyecta el puerto de salida correspondiente.
- Los errores de negocio se traducen a HTTP en un solo lugar (`RestExceptionHandler`): `404` no existe, `409` ya existe, `400` datos inválidos.

## Requisitos previos

- **JDK 25** (`java -version`)
- **Maven 3.9+** (`mvn -version`)
- **Docker Desktop** (para levantar las bases y/o la aplicación) o bien MariaDB y MongoDB instalados localmente
- **Lombok** configurado en el IDE (VS Code: extensión *Language Support for Java*; IntelliJ/Eclipse: instalar el plugin de Lombok y habilitar *annotation processing*)
- Git

## Credenciales y puertos

| Servicio | Puerto en el host | Usuario / clave de la app | Base |
|---|---|---|---|
| MariaDB | **3307** | `persona_db` / `persona_db` | `persona_db` |
| MongoDB | **27017** | `persona_db` / `persona_db` (authSource `admin`) | `persona_db` |
| API REST | **3000** | - | - |

Las credenciales *root* de los contenedores están en `.env` (solo las usa Docker; la aplicación usa `persona_db`).

## Scripts DDL y DML

Carpeta `scripts/`:

| Archivo | Contenido |
|---|---|
| `persona_ddl_maria.sql` | Crea usuario, esquema y tablas `persona`, `profesion`, `telefono`, `estudios` (con llaves foráneas) |
| `persona_dml_maria.sql` | Datos de ejemplo de personas |
| `persona_ddl_mongo.js` | Crea el usuario `persona_db` con permisos sobre la base `persona_db` |
| `persona_dml_mongo.js` | Datos de ejemplo de personas (colección `persona`) |

## Opción A: todo con Docker (recomendado)

`docker-compose.yml` levanta MariaDB, MongoDB y la API REST. Los scripts DDL/DML se ejecutan automáticamente **la primera vez** que se crean los volúmenes.

```bash
# 1. Bases de datos + API REST (compila el proyecto dentro de la imagen)
docker compose up -d --build

# 2. Ver logs de la API
docker compose logs -f rest

# 3. CLI interactiva (no se levanta con "up")
docker compose run --rm cli

# 4. Detener
docker compose down

# 5. Detener y borrar datos (para volver a ejecutar los scripts desde cero)
docker compose down -v
```

Solo las bases (para correr la aplicación desde el IDE o con `java -jar`):

```bash
docker compose up -d mariadb mongodb
```

### Problemas frecuentes en Windows

- **"WSL needs updating"** en Docker Desktop: abrir PowerShell **como administrador**, ejecutar `wsl --update`, reiniciar Docker Desktop y pulsar *Try Again*.
- **"Docker daemon is not running"**: abrir Docker Desktop y esperar a que el motor indique *Engine running*.
- **Puerto 3307 o 27017 ocupado**: detener la instalación local de MariaDB/MongoDB o cambiar el puerto del lado izquierdo en `docker-compose.yml`.

## Opción B: instalación local de las bases

1. Instalar **MariaDB** en el puerto **3307** y **MongoDB** en el **27017**.
2. Ejecutar los scripts como administrador de cada motor:

```bash
# MariaDB (usuario root)
mariadb -h 127.0.0.1 -P 3307 -u root -p < scripts/persona_ddl_maria.sql
mariadb -h 127.0.0.1 -P 3307 -u root -p < scripts/persona_dml_maria.sql

# MongoDB (los scripts usan "use <db>", por eso se envían por la entrada estándar)
mongosh -u <admin> -p <clave> --authenticationDatabase admin < scripts/persona_ddl_mongo.js
mongosh -u <admin> -p <clave> --authenticationDatabase admin < scripts/persona_dml_mongo.js
```

## Compilación

Desde la raíz del repositorio:

```bash
mvn -DskipTests clean package
```

Debe terminar con `BUILD SUCCESS` en los 7 módulos y generar:

- `rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar`
- `cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar`

## Ejecución

Con las bases arriba (Docker u Opción B):

```bash
# API REST (puerto 3000)
java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar

# CLI (menú interactivo)
java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar
```

También se pueden ejecutar desde el IDE: `PersonAppRestApi` (REST) y `PersonAppCli` (CLI). Son **dos aplicaciones Spring Boot distintas**.

La configuración está en `src/main/resources/application.properties` de cada adaptador de entrada. Dentro de Docker se sobrescribe con variables de entorno (`SPRING_DATASOURCE_URL`, `SPRING_MONGODB_HOST`, `SPRING_MONGODB_PORT`).

## Swagger / OpenAPI

- Interfaz: http://localhost:3000/swagger-ui.html
- Definición JSON: http://localhost:3000/api-docs

## Endpoints REST

En todos, `{database}` es `MARIA` o `MONGO` (no distingue mayúsculas).

### Persona - `/api/v1/persona`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/{database}` | Listar personas |
| GET | `/{database}/count` | Contar personas |
| GET | `/{database}/{cc}` | Buscar una persona |
| GET | `/{database}/{cc}/telefonos` | Teléfonos de la persona |
| GET | `/{database}/{cc}/estudios` | Estudios de la persona |
| POST | `/` | Crear (`201`; `409` si existe) |
| PUT | `/` | Editar (`404` si no existe) |
| DELETE | `/{database}/{cc}` | Eliminar (borra también sus teléfonos y estudios) |

### Estudio - `/api/v1/estudio` (llave compuesta `ccPer` + `idProf`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/{database}` | Listar estudios |
| GET | `/{database}/count` | Contar estudios |
| GET | `/{database}/{ccPer}/{idProf}` | Buscar un estudio |
| GET | `/{database}/{ccPer}/{idProf}/persona` | Persona del estudio |
| GET | `/{database}/{ccPer}/{idProf}/profesion` | Profesión del estudio |
| POST | `/` | Crear |
| PUT | `/` | Editar |
| DELETE | `/{database}/{ccPer}/{idProf}` | Eliminar |

### Profesión - `/api/v1/profesion`

CRUD completo (listar, contar, buscar, crear, editar, eliminar) y `GET /{database}/{id}/estudios` para los estudios de la profesión. Al eliminar una profesión se eliminan sus estudios en ambas bases.

### Teléfono - `/api/v1/telefono`

CRUD completo (listar, contar, buscar, crear, editar, eliminar) y `GET /{database}/{number}/duenio` para el dueño del teléfono. Al crear o editar se valida que el dueño exista (`400` si no).

> Los cuerpos exactos de `Profesion` y `Telefono` están documentados en Swagger.

### Ejemplos de cuerpo (JSON)

Crear persona (`POST /api/v1/persona`):

```json
{
  "dni": "1001",
  "firstName": "Ana",
  "lastName": "Gomez",
  "age": "25",
  "sex": "F",
  "database": "MARIA"
}
```

Crear estudio (`POST /api/v1/estudio`):

```json
{
  "personDni": "123456789",
  "professionId": "1",
  "universityName": "Pontificia Universidad Javeriana",
  "graduationDate": "2024-06-15",
  "database": "MONGO"
}
```

### Códigos de respuesta

| Código | Cuándo |
|---|---|
| 200 / 201 | Operación exitosa / recurso creado |
| 400 | Dato inválido, base de datos inválida, o dueño/persona/profesión inexistente al crear teléfono o estudio |
| 404 | El recurso no existe (buscar, editar, eliminar) |
| 409 | El recurso ya existe (crear) |

## CLI

Al ejecutar la CLI aparece el menú principal con los módulos **Persona, Profesión, Teléfono y Estudio**. En cada uno se elige primero el motor (1 = MariaDB, 2 = MongoDB) y luego la operación: ver todos, buscar, contar, crear, editar, eliminar y consultas de agregación.

## Estructura del repositorio

```
.
├── common/ domain/ application/
├── maria-output-adapter/ mongo-output-adapter/
├── rest-input-adapter/ cli-input-adapter/
├── scripts/                 DDL y DML de MariaDB y MongoDB
├── docker/mongo-init/       script de carga inicial de MongoDB en Docker
├── docker-compose.yml  Dockerfile  .env
└── pom.xml                  POM padre (BOM de Spring Boot, Lombok, compilador)
```

## Flujo de trabajo en Git

```bash
git checkout develop && git pull
git checkout -b feat/<mi-parte>
# ... cambios ...
git add . && git commit -m "mensaje"
git push -u origin feat/<mi-parte>
```

Luego abrir un *Pull Request* hacia `develop`.

## Licencia

Apache License 2.0 (ver `LICENSE`).
