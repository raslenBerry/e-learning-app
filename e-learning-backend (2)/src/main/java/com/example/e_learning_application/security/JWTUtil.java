package com.example.e_learning_application.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a secure key for HS256

    // Generate JWT token
    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // 1 hour expiration time
                .signWith(SECRET_KEY)
                .compact();
        logger.info("Generated Token: {}", token);
        return token;
    }

    // Extract Claims from the token
    public Claims extractClaims(String token) {
        logger.info("Parsing Token: {}", token);
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract username from the token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Validate the token
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}