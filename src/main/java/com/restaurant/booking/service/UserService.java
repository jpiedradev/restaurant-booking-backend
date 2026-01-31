package com.restaurant.booking.service;

import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario por ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Crea un nuevo usuario (solo para ADMIN)
     * Los usuarios regulares usan /api/auth/register
     */
    public User createUser(User user) {
        // Validar email único
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + user.getEmail());
        }

        // Validar username único
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("El username ya está en uso: " + user.getUsername());
        }

        // IMPORTANTE: Encriptar la contraseña
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Si no se especifica rol, CUSTOMER por defecto
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CUSTOMER");
        }

        // Activar cuenta por defecto
        user.setEnabled(true);
        user.setAccountNonLocked(true);

        return userRepository.save(user);
    }

    /**
     * Actualiza un usuario
     */
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Validar email único (si cambió)
        if (!existingUser.getEmail().equals(userDetails.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("El email ya está registrado: " + userDetails.getEmail());
            }
        }

        // Validar username único (si cambió)
        if (!existingUser.getUsername().equals(userDetails.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new RuntimeException("El username ya está en uso: " + userDetails.getUsername());
            }
        }

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhone(userDetails.getPhone());
        existingUser.setRole(userDetails.getRole());

        return userRepository.save(existingUser);
    }

    /**
     * Elimina un usuario
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Busca un usuario por email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    /**
     * Busca un usuario por username
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));
    }
}