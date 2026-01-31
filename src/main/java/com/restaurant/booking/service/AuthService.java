package com.restaurant.booking.service;

import com.restaurant.booking.dto.auth.LoginRequest;
import com.restaurant.booking.dto.auth.LoginResponse;
import com.restaurant.booking.dto.auth.RegisterRequest;
import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.UserRepository;
import com.restaurant.booking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Registra un nuevo usuario en el sistema
     */
    public User register(RegisterRequest request) {
        // 1. Validar que el username no exista
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya está en uso: " + request.getUsername());
        }

        // 2. Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + request.getEmail());
        }

        // 3. Crear el nuevo usuario
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        // 4. Encriptar la contraseña
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 5. Asignar rol por defecto
        user.setRole("CUSTOMER");

        // 6. Activar la cuenta
        user.setEnabled(true);
        user.setAccountNonLocked(true);

        // 7. Guardar en la base de datos
        return userRepository.save(user);
    }

    /**
     * Autentica un usuario y genera un token JWT
     */
    public LoginResponse login(LoginRequest request) {
        try {
            // 1. Autenticar con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        // 2. Cargar los detalles del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // 3. Generar el token JWT
        final String jwt = jwtUtil.generateToken(userDetails);

        // 4. Obtener información completa del usuario
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 5. Crear la respuesta
        return new LoginResponse(
                jwt,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    /**
     * Valida si un username está disponible
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Valída si un email está disponible
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}