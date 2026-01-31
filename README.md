# Restaurant Booking System - Backend

API REST desarrollada con Spring Boot para un sistema de gestión de reservas de restaurante.

## 🚀 Tecnologías

- Java 17/21
- Spring Boot 3.2.x
- Spring Data JPA
- MySQL 8
- Maven
- Lombok

## 📋 Características

- ✅ Gestión completa de mesas (CRUD)
- ✅ Gestión de usuarios con roles (CUSTOMER, STAFF, ADMIN)
- ✅ Sistema de reservas con estados (PENDING, CONFIRMED, SEATED, COMPLETED, CANCELLED, NO_SHOW)
- ✅ Validación de disponibilidad en tiempo real
- ✅ Sincronización automática de estados entre reservas y mesas
- ✅ API RESTful con documentación clara

## 🗄️ Modelo de Datos

### Entidades principales:
- **RestaurantTable**: Mesas del restaurante (número, capacidad, ubicación, estado)
- **User**: Usuarios del sistema (username, email, rol)
- **Reservation**: Reservas (fecha, hora, comensales, estado)

### Relaciones:
- User 1:N Reservation
- RestaurantTable 1:N Reservation

## ⚙️ Configuración

1. **Base de datos MySQL:**
```sql
CREATE DATABASE restaurant_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **application.properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_booking
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

3. **Ejecutar:**
```bash
./mvnw spring-boot:run
```

El servidor iniciará en `http://localhost:8081`

## 📡 Endpoints Principales

### Mesas
- `GET /api/tables` - Listar todas
- `GET /api/tables/available` - Mesas disponibles
- `POST /api/tables` - Crear mesa
- `PUT /api/tables/{id}` - Actualizar mesa
- `DELETE /api/tables/{id}` - Eliminar mesa

### Usuarios
- `GET /api/users` - Listar usuarios
- `POST /api/users` - Crear usuario
- `PUT /api/users/{id}` - Actualizar usuario

### Reservas
- `GET /api/reservations` - Listar reservas
- `GET /api/reservations/date/{date}` - Reservas por fecha
- `GET /api/reservations/check-availability` - Verificar disponibilidad
- `POST /api/reservations` - Crear reserva
- `PATCH /api/reservations/{id}/status` - Cambiar estado
- `DELETE /api/reservations/{id}` - Eliminar reserva

## 🏗️ Arquitectura
```
Controller → Service → Repository → Entity → Database
```

- **Controllers**: Endpoints REST
- **Services**: Lógica de negocio y validaciones
- **Repositories**: Acceso a datos (Spring Data JPA)
- **DTOs**: Transferencia de datos entre capas

## 👨‍💻 Autor

[JOHAN PIEDRA]


