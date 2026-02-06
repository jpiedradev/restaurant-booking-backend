# Restaurant Booking System - Backend

API REST desarrollada con Spring Boot para un sistema de gestión de reservas de restaurante.

## 🚀 Tecnologías

- Java 17/21
- Spring Boot 3.2.x
- Spring Data JPA
- Spring Security + JWT
- MySQL 8
- JavaMail (SMTP)
- Maven
- Lombok

## 📋 Características

### Gestión de Datos
- ✅ Gestión completa de mesas (CRUD)
- ✅ Gestión de usuarios con roles (CUSTOMER, STAFF, ADMIN)
- ✅ Sistema de reservas con estados (PENDING, CONFIRMED, SEATED, COMPLETED, CANCELLED, NO_SHOW)
- ✅ Validación de disponibilidad en tiempo real
- ✅ Sincronización automática de estados entre reservas y mesas

### Seguridad y Autenticación
- ✅ Autenticación con JWT (JSON Web Tokens)
- ✅ Contraseñas encriptadas con BCrypt
- ✅ Control de acceso por roles con @PreAuthorize
- ✅ Endpoints públicos y protegidos
- ✅ Validación de permisos en tiempo real

### Notificaciones
- ✅ Emails automáticos de bienvenida al registrarse
- ✅ Confirmación de reserva creada
- ✅ Notificación de reserva confirmada
- ✅ Notificación de reserva cancelada

## 🗄️ Modelo de Datos

### Entidades principales:
- **RestaurantTable**: Mesas del restaurante (número, capacidad, ubicación, estado)
- **User**: Usuarios del sistema (username, email, rol, password encriptado)
- **Reservation**: Reservas (fecha, hora, comensales, estado)

### Relaciones:
- User 1:N Reservation
- RestaurantTable 1:N Reservation

## ⚙️ Configuración

### 1. Base de datos MySQL:
```sql
CREATE DATABASE restaurant_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. application.properties:
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_booking
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update

# Server
server.port=8080

# JWT
jwt.secret=tu-clave-secreta-super-segura-de-al-menos-256-bits
jwt.expiration=86400000

# Email (Configurar con tu propio Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password-de-16-caracteres
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
app.mail.from=tu-email@gmail.com
app.mail.from-name=Restaurant Booking System
```

**Nota:** Para obtener tu App Password de Gmail:
1. Ve a tu cuenta de Google → Seguridad
2. Activa verificación en 2 pasos
3. Busca "Contraseñas de aplicaciones"
4. Genera una para "Mail"

### 3. Ejecutar:
```bash
./mvnw spring-boot:run
```

El servidor iniciará en `http://localhost:8080`

## 📡 Endpoints Principales

### Autenticación (Públicos)
- `POST /api/auth/register` - Registrar usuario
- `POST /api/auth/login` - Iniciar sesión (retorna JWT)
- `GET /api/auth/check-username` - Verificar disponibilidad de username
- `GET /api/auth/check-email` - Verificar disponibilidad de email

### Mesas (Requiere autenticación)
- `GET /api/tables` - Listar todas (ADMIN, STAFF)
- `GET /api/tables/available` - Mesas disponibles (ADMIN, STAFF, CUSTOMER)
- `POST /api/tables` - Crear mesa (ADMIN)
- `PUT /api/tables/{id}` - Actualizar mesa (ADMIN)
- `PATCH /api/tables/{id}/status` - Cambiar estado (ADMIN, STAFF)
- `DELETE /api/tables/{id}` - Eliminar mesa (ADMIN)

### Usuarios (Solo ADMIN)
- `GET /api/users` - Listar usuarios
- `GET /api/users/email/{email}` - Buscar por email
- `POST /api/users` - Crear usuario (STAFF, ADMIN)
- `PUT /api/users/{id}` - Actualizar usuario
- `DELETE /api/users/{id}` - Eliminar usuario

### Reservas (Requiere autenticación)
- `GET /api/reservations` - Listar reservas (ADMIN/STAFF: todas, CUSTOMER: propias)
- `GET /api/reservations/my-reservations` - Mis reservas (CUSTOMER)
- `GET /api/reservations/check-availability` - Verificar disponibilidad (Público)
- `POST /api/reservations` - Crear reserva (Todos)
- `PATCH /api/reservations/{id}/status` - Cambiar estado (ADMIN, STAFF)
- `PATCH /api/reservations/{id}/cancel` - Cancelar reserva (Dueño, ADMIN, STAFF)
- `DELETE /api/reservations/{id}` - Eliminar reserva (ADMIN)

## 🔐 Seguridad

### Roles y Permisos

**CUSTOMER:**
- Ver y crear sus propias reservas
- Cancelar sus propias reservas
- Crear nuevas reservas

**STAFF:**
- Ver todas las reservas y mesas
- Confirmar, sentar y completar reservas
- Cambiar estado de mesas
- Dashboard especializado

**ADMIN:**
- Acceso completo a todas las funcionalidades
- Gestión de usuarios
- Gestión de mesas (CRUD completo)
- Gestión de reservas

### Autenticación JWT

Todas las peticiones protegidas deben incluir el header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 🏗️ Arquitectura
```
Controller → Service → Repository → Entity → Database
             ↓
         EmailService (Async)
```

- **Controllers**: Endpoints REST con validación de roles
- **Services**: Lógica de negocio y validaciones
- **Repositories**: Acceso a datos (Spring Data JPA)
- **DTOs**: Transferencia de datos entre capas
- **Security**: JWT, filtros de autenticación, configuración de CORS
- **EmailService**: Envío asíncrono de notificaciones

## 📧 Sistema de Notificaciones

Emails HTML automáticos enviados en:
- Registro de nuevo usuario
- Creación de reserva (PENDING)
- Confirmación de reserva (STAFF aprueba)
- Cancelación de reserva

Los emails se envían de forma **asíncrona** (no bloquean la respuesta).

## 🧪 Testing

Se incluyen configuraciones probadas con:
- Postman (colecciones de prueba)
- Validaciones de permisos por rol
- Flujos completos de reserva

## 👨‍💻 Autor

Johan Piedra
