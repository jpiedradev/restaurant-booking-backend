package com.restaurant.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El username es obligatorio")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String phone;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "CUSTOMER"; // CUSTOMER, ADMIN, STAFF

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // NUEVO: Campo para activar/desactivar usuarios
    @Column(nullable = false)
    private Boolean enabled = true;

    // NUEVO: Campo para bloquear cuentas
    @Column(nullable = false)
    private Boolean accountNonLocked = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}