package com.acme.encuestas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // 1) Permitir sin token las rutas públicas (opcionales: no necesario si simplemente dejamos pasar cuando no hay token)
        // if (uri.startsWith("/api/auth/") || uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui")) {
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        // 2) Si NO hay Authorization: Bearer ...  => dejar pasar
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Validar token si viene
        String token = authHeader.substring(7);
        String subject = jwtUtil.validateTokenAndGetSubject(token);
        if (subject != null) {
            String role = jwtUtil.getRoleFromToken(token);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    subject,
                    null,
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
