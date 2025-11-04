package com.arka.MSAuthentication.infrastructure.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey signingKey; // La clave secreta para firmar el JWT
    private final long expirationTime; // Tiempo de expiración en milisegundos

    //Constructor: Inicializa la clave
    public JwtUtil(@Value("${jwt.secret-key}") String secretKey,
                   @Value("${jwt.expiration}") long expirationTime) {

        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    // Método para generar el Token
    public String generateToken(Authentication authentication, Long userId) {

        // Obtiene el nombre de usuario (típicamente el email)
        String subject = authentication.getName();
        Instant now = Instant.now();

        // Extraer authorities del usuario autenticado
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(subject) // 'sub' (Subject/Sujeto)
                .claim("authorities", authorities) // Agrega los roles como un claim personalizado
                .claim("id", userId) // Agrega el ID del usuario como un claim personalizado
                .setIssuedAt(Date.from(now)) // 'iat' (Issued At/Emitido En)
                .setExpiration(Date.from(now.plusMillis(expirationTime))) // 'exp' (Expiration/Expiración)
                // Firma el token con la clave y el algoritmo HS256 (por defecto para esta clave)
                .signWith(this.signingKey, Jwts.SIG.HS256)
                .compact(); // Genera el token final como String compacto
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Token inválido, expirado o con firma incorrecta
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Long getIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("id", Long.class);
    }

    //Método para extraer authorities del JWT
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)  // ← Usar signingKey en lugar de secret
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String authoritiesString = claims.get("authorities", String.class);

        if (authoritiesString == null || authoritiesString.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(authoritiesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
