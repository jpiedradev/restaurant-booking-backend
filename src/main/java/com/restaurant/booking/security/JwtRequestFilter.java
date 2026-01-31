package com.restaurant.booking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Intercepta cada petición HTTP y valida el token JWT
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraer el header "Authorization"
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 2. Verificar que el header existe y empieza con "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extraer el token (quitar "Bearer ")
            jwt = authorizationHeader.substring(7);

            try {
                // Extraer el username del token
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Token inválido o expirado
                logger.error("Error al extraer username del token: " + e.getMessage());
            }
        }

        // 3. Si hay username y no hay autenticación previa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar el usuario desde la base de datos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validar el token
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Agregar detalles de la petición
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // 4. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}