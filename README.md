# API REST para el acortamiento y redirección de URLs
Este proyecto es una solución a una propuesta de proyecto en 
https://roadmap.sh/projects/url-shortening-service
Cuenta con un backend de nivel profesional construida con **Spring Boot 3**, 
diseñada para optimizar y gestionar la redirección de enlaces web de manera eficiente. 
Aplica principios de arquitectura limpia, aislamiento de datos mediante DTOs, manejo atómico 
de concurrencia en bases de datos y despliegues completamente dockerizados.

## Tecnologías Utilizadas

* **Lenguaje:** Java 17 (OpenJDK)
* **Framework Principal:** Spring Boot (Spring Web, Spring Data JPA)
* **Base de Datos:** MySQL 
* **Mapeo de Objetos:** MapStruct 1.6.3 (Compilación optimizada con Lombok)
* **Utilidades:** Project Lombok
* **Contenedores y DevOps:** Docker & Docker Compose (Multi-stage build)

## Características Destacadas

* **Arquitectura Desacoplada (DTO Segregation):** Las entidades JPA nunca se exponen directamente al cliente. Se implementaron mappers automáticos en tiempo de compilación con **MapStruct** para garantizar la transferencia segura y limpia de información.
* **Control de Concurrencia Atómica:** Incremento del contador de visitas directamente en la base de datos mediante queries `@Modifying`, mitigando condiciones de carrera (*race conditions*) ante tráfico masivo simultáneo.
* **Manejo Nativo del Protocolo HTTP:**
  * Redirección instantánea mediante código de estado **`302 FOUND`** inyectando cabeceras `Location`.
  * Control estricto de caducidad temporal emitiendo un estado **`410 GONE`** si el enlace ha expirado.
* **Infraestructura Portable:** Configuración externalizada mediante variables de entorno y empaquetado multi-etapa con Docker.

---

## Estructura de la Base de Datos

La persistencia se maneja sobre un esquema relacional optimizado, con restricciones de unicidad e indexación implícita en el código corto de búsqueda.

```sql
CREATE TABLE url (
  id_url INT AUTO_INCREMENT PRIMARY KEY,
  url VARCHAR(255) NOT NULL,
  short_code VARCHAR(10) NOT NULL UNIQUE,
  created_at DATETIME(6),
  updated_at DATETIME(6),
  expiration_date DATETIME(6),
  access_count INT NOT NULL
);
```

---

## Documentación de la API (Endpoints)
El servicio base responde en la raíz del contexto `/api`.

### 1. Crear Código Corto
Genera de manera aleatoria un hash único de 6 caracteres asignándole un tiempo de vida por defecto.

* **URL:** `/api/create`
* **Método:** `POST`
* **Parámetros de Consulta (Query):** `url` (String)
* **Código de Respuesta:** `201 CREATED` *(Nota: Ajustado de 210 al estándar HTTP 201)*
* **Ejemplo de Respuesta (JSON):**

```json
{
    "idUrl": 1,
    "url": "https://google.com",
    "shortCode": "45ad9e",
    "createdAt": "2026-05-24T03:47:40.298097",
    "updatedAt": null,
    "expirationDate": "2026-06-03T03:47:40.298123",
    "accessCount": 0
}
```
### 2. Redirección de URL
Busca el destino original, incrementa las métricas de acceso de forma segura y redirige al navegador o cliente.
* **URL:** `/api/{code}`
* **Método:** `GET`
* **Códigos de Respuesta:**
* 302 FOUND: Redirección exitosa (Contiene cabecera Location: <url_larga>).
* 410 GONE: El enlace existía pero superó su fecha de expiración.
* 404 NOT FOUND: El código ingresado no existe en el sistema.

### 3. Modificar Destino de URL
Permite actualizar la URL larga a la que apunta un código corto existente, renovando su tiempo de expiración.
* **URL:** `/api/update`
* **Método:** `PATCH`
* **Cuerpo de la Petición (JSON - RequestBody):**

  ```json
  {
    "url": "https://www.youtube.com",
    "shortCode": "6566e8"
  }
  ```
* Código de Respuesta: `202 ACCEPTED`
* Cuerpo de la Respuesta: JSON idéntico a la estructura de creación con los campos actualizados.

### 4. Consultar Estadísticas
Obtiene las métricas de uso y metadatos del enlace sin activar una redirección ni alterar el contador de visitas.
* **URL:** `/api/{code}/stats`
* **Método:** `GET`
* **Código de Respuesta:** `200 OK`
* Ejemplo de Uso: `GET http://localhost:8081/api/6566e8/stats`

### 5. Eliminar Enlace
Remueve de forma lógica y física el registro correspondiente de la base de datos.
* **URL:** `/api/delete`
* **Método:** `DELETE`
* Parámetros de Consulta (Query): `url` (Se pasa el `shortCode` a eliminar)
* Código de Respuesta: `204 NO CONTENT`


---

## Instalación y Configuración (Ejecución Local)

Si prefieres ejecutar el proyecto directamente en tu entorno local sin utilizar Docker, sigue estos pasos:

### Requisitos Previos

Antes de ejecutar la aplicación, asegúrate de tener instalado:

* **Java 17** o superior.
* **Maven** para la gestión de dependencias y compilación.
* **MySQL Server** activo en tu máquina local.

---

### Pasos para la Ejecución

#### 1. Clona el repositorio

```bash
git clone https://github.com/TU_USUARIO/acortador-url.git
cd acortador-url

```

#### 2. Configura la Base de Datos

La aplicación está diseñada para buscar variables de entorno. Si ejecutas de forma local en tu IDE o terminal, puedes crear una base de datos llamada `acortador_db` en tu MySQL local.

El archivo `src/main/resources/application.properties` ya viene preconfigurado con los siguientes valores por defecto para agilizar tu desarrollo:

```properties
spring.application.name=Acortador

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/acortador_db?createDatabaseIfNotExist=true}}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:mysql}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

```

*(Nota: Si las credenciales de tu MySQL local son diferentes a `root` y `mysql123`, puedes cambiarlas directamente en este archivo antes de compilar).*

#### 3. Compila el proyecto con Maven

Usa el Wrapper de Maven incluido en el proyecto para descargar las dependencias (incluyendo el procesador de MapStruct) y empaquetar el código:

```bash
./mvnw clean package -DskipTests
```

*(En entornos Windows, utiliza `mvnw clean package -DskipTests` o `mvn clean package -DskipTests`).*

#### 4. Ejecuta la aplicación

Una vez terminada la compilación con éxito, arranca el servidor embebido de Tomcat ejecutando:

```bash
./mvnw spring-boot:run
```

#### 5. Accede al servicio

El servidor web se levantará en el puerto predeterminado. Ya puedes empezar a realizar peticiones HTTP locales apuntando a la raíz del contexto:

```text
http://localhost:8080/api
```
---
## Despliegue con Docker (Recomendado)

Este proyecto utiliza una construcción **Multi-stage** en el `Dockerfile`, por lo que **no necesitas instalar Java ni Maven localmente**.  
Todo el proceso de compilación y ejecución se realiza de forma aislada dentro de los contenedores.

### Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### Pasos para Ejecutar:

1. Clona este repositorio en tu máquina local.
2. Abre una terminal en la raíz del proyecto (donde se encuentra el archivo `docker-compose.yml`).
3. Ejecuta el siguiente comando para compilar y levantar toda la infraestructura (Backend + MySQL) en segundo plano:

```bash
docker compose up --build -d
```
---
