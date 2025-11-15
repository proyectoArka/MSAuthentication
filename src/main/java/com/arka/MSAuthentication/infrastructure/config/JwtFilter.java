package com.arka.MSAuthentication.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    //private final UserDetailsService userDetailsService;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        //this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Excluir las rutas de autenticación
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/consuluser/") ||
                path.startsWith("/api/v1/auth/consuluser/") ||
                path.equals("/api/auth/updateuserinfo")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. validar que el header Authorization valido
        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. valiadar que el JWT sea valido
        String jwt = authorizationHeader.substring(7);
        if(!this.jwtUtil.validateToken(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. cargar el usuario del UserDetailsService
        String username = this.jwtUtil.getEmailFromToken(jwt);
        List<GrantedAuthority> authorities = this.jwtUtil.getAuthoritiesFromToken(jwt);
        //User user = (User) this.userDetailsService.loadUserByUsername(username);

        // 4. cargar el usuario en el contexto de seguridad
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
               username, null, authorities
                // user.getUsername(), user.getClass(), user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

    }
}
