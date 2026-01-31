package com.restaurant.booking.security;

import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga un usuario por su username
     * Este método es llamado automáticamente por Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuario en la base de datos
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username
                ));

        // Convertir a UserDetails de Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                user.getAccountNonLocked(),
                getAuthorities(user)
        );
    }

    /**
     * Convierte el rol del usuario a authorities de Spring Security
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // Por ahora, cada usuario tiene un solo rol
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
    }
}