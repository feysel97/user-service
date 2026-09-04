package com.microservice.userservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final SecretKey mainSigningKey;

    // Injecting the secret string from your application.yml
    public JwtTokenProvider(@Value("${app.jwt.secret}") String secretKeyString) {
        try {
            System.out.println("Initializing JwtTokenProvider with key length: " + secretKeyString.length());
            byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
            this.mainSigningKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to initialize JwtTokenProvider key!");
            e.printStackTrace();
            throw e;
        }
    }

    // Validates the token structure and expiration against the shared secret signature
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(mainSigningKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // TEMPORARY: Print the exact error to the console for debugging
            System.out.println("JWT Validation Failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Extracts the username (Subject) from the token
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // Extracts roles/authorities claim from the token
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaims(token);
        // Assumes your Auth Service puts roles under a "roles" claim array
        return claims.get("roles", List.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(mainSigningKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}