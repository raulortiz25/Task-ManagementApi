# Task Management API

API REST desarrollada con Java y Spring Boot para la gestión de tareas personales mediante autenticación segura y control de acceso por usuario.

Este proyecto demuestra conceptos de desarrollo backend como autenticación mediante JWT, seguridad con Spring Security, persistencia de datos, validación de entradas, manejo de excepciones y pruebas unitarias.

## Características

- **Autenticación de usuarios:** Registro e inicio de sesión.
- **Autenticación mediante JWT:** Uso de JSON Web Tokens para gestionar sesiones de forma sin estado.
- **Gestión de tareas:** Crear, consultar, actualizar y eliminar tareas.
- **Control de acceso por usuario:** Cada usuario solo puede acceder y administrar sus propias tareas.
- **Seguridad de contraseñas:** Cifrado mediante BCrypt.
- **Validación de datos:** Validación de solicitudes mediante Jakarta Bean Validation.
- **Manejo de excepciones:** Excepciones personalizadas para gestionar errores de la aplicación.
- **Pruebas unitarias:** Pruebas de la capa de servicios utilizando JUnit y Mockito.
- **Persistencia de datos:** Almacenamiento de información mediante Spring Data JPA y MySQL.

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje de programación |
| Spring Boot 4.0.7 | Framework para el desarrollo backend |
| Spring Security | Autenticación y autorización |
| JWT (Auth0 Java JWT) | Gestión de tokens de autenticación |
| Spring Data JPA | Acceso y persistencia de datos |
| Hibernate | Mapeo objeto-relacional |
| MySQL | Base de datos relacional |
| Jakarta Bean Validation | Validación de datos |
| Lombok | Reducción de código repetitivo |
| JUnit y Mockito | Pruebas unitarias |
| Maven | Gestión de dependencias y compilación |
| Postman | Pruebas de los endpoints |

## Arquitectura del proyecto

La aplicación utiliza una arquitectura por capas que separa las solicitudes HTTP, la lógica de negocio y el acceso a los datos.

```text
Cliente / Postman
        |
        v
Capa de Controladores
        |
        v
Capa de Servicios
        |
        v
Capa de Repositorios
        |
        v
Base de datos MySQL
```

- **Controladores:** Reciben las solicitudes HTTP y devuelven las respuestas.
- **Servicios:** Contienen la lógica de negocio y las reglas de acceso a las tareas.
- **Repositorios:** Gestionan las operaciones con la base de datos mediante Spring Data JPA.
- **DTOs:** Separan los datos de entrada y salida de las entidades utilizadas para la persistencia.
- **Seguridad:** Protege los endpoints mediante JWT y Spring Security.

## Requisitos previos

Para ejecutar el proyecto necesitas:

- JDK 17 o una versión compatible.
- MySQL Server.
- Git.
- Un entorno de desarrollo compatible con Java, como IntelliJ IDEA.

El proyecto incluye Maven Wrapper, por lo que no necesitas instalar Maven por separado.

## Instalación y ejecución

### 1. Clonar el repositorio

Abre una terminal y ejecuta los siguientes comandos:

```bash
git clone https://github.com/raulortiz25/Task-ManagementApi.git
cd Task-ManagementApi
```

### 2. Crear la base de datos

En MySQL, ejecuta la siguiente instrucción:

```sql
CREATE DATABASE taskmanagementapi;
```

### 3. Configurar las variables de entorno

La aplicación necesita las credenciales de la base de datos y una clave secreta para firmar los tokens JWT.

**Linux / macOS:**

```bash
export DB_USERNAME="tu_usuario_mysql"
export DB_PASSWORD="tu_contraseña_mysql"
export JWT_SECRET="tu_clave_secreta"
```

**Windows PowerShell:**

```powershell
$env:DB_USERNAME="tu_usuario_mysql"
$env:DB_PASSWORD="tu_contraseña_mysql"
$env:JWT_SECRET="tu_clave_secreta"
```

Reemplaza los valores de ejemplo por tus propias credenciales. Utiliza una clave JWT larga, aleatoria y segura. No subas contraseñas ni claves secretas a GitHub.

### 4. Ejecutar la aplicación

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación está configurada para ejecutarse en:

```text
http://localhost:9090
```

### 5. Ejecutar las pruebas

Para ejecutar las pruebas del proyecto, utiliza:

```bash
./mvnw test
```

En Windows:

```powershell
.\mvnw.cmd test
```

## Endpoints de la API

### Gestión de tareas

Ruta base: `/api/task`

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| GET | `/api/task` | Obtener las tareas del usuario autenticado |
| POST | `/api/task` | Crear una nueva tarea |
| PUT | `/api/task/{id}` | Actualizar una tarea existente |
| DELETE | `/api/task/{id}` | Eliminar una tarea |

Los endpoints protegidos requieren un token JWT válido. Las operaciones sobre tareas respetan las reglas de acceso del usuario autenticado.

### Autenticación

La aplicación también cuenta con funcionalidades de registro e inicio de sesión. Para conocer las rutas exactas y el formato de las solicitudes, consulta el controlador de autenticación del proyecto.

## Autenticación mediante JWT

Después de iniciar sesión, utiliza el token JWT recibido para acceder a los endpoints protegidos.

Incluye el siguiente encabezado en tus solicitudes HTTP:

```http
Authorization: Bearer TU_TOKEN_JWT
```

La aplicación utiliza autenticación sin estado (*stateless*), por lo que el cliente debe enviar el token en cada solicitud que requiera autenticación.

## Pruebas unitarias

El proyecto incluye pruebas unitarias de la capa de servicios utilizando JUnit y Mockito.

Estas pruebas permiten comprobar el comportamiento de la lógica de negocio en diferentes escenarios, entre ellos:

- Operaciones exitosas sobre las tareas.
- Intentos de acceso a tareas pertenecientes a otros usuarios.
- Usuarios o tareas que no existen.
- Manejo de errores durante las operaciones.

Para comprobar el funcionamiento del proyecto, ejecuta la suite de pruebas antes de realizar cambios o publicar nuevas versiones.

## Posibles mejoras futuras

Algunas mejoras que podrían incorporarse en futuras versiones son:

- Documentación de la API mediante OpenAPI y Swagger UI.
- Pruebas de integración para controladores, seguridad y base de datos.
- Configuración mediante Docker.
- Integración continua con GitHub Actions.
- Migraciones de base de datos y configuración específica para producción.

## Autor

**Raúl Ortiz**

[GitHub](https://github.com/raulortiz25)

---

Proyecto desarrollado con fines de aprendizaje y práctica de desarrollo de API REST seguras utilizando Java y Spring Boot.