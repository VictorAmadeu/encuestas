package com.acme.encuestas.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final int expirationMinutes;
    private final String issuer;

    public JwtUtil(@Value("${jwt.secret}") String secretBase64,
                   @Value("${jwt.accessTokenMinutes}") int expirationMinutes,
                   @Value("${jwt.issuer}") String issuer) {
        // Decodificar Base64 -> bytes reales (>= 32) -> clave HS256 válida
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMinutes = expirationMinutes;
        this.issuer = issuer;
    }

    public String generateToken(String userId, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMinutes * 60L * 1000L);

        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Devuelve el subject (userId) si el token es válido; en otro caso null. */
    public String validateTokenAndGetSubject(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    /** Extrae el rol del claim "role". */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (JwtException e) {
            return null;
        }
    }
}
