package com.military.asset.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Base64;

@Component
public class JwtUtil {
    private final String SECRET_KEY_STRING = "bXktc2VjcmV0LWtleS1mb3Itand0LXRva2Vucy1tdXN0LWJlLWF0LWxlYXN0LTI1Ni1iaXRzLWxvbmc=";
    private final SecretKey SECRET_KEY;
    private final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24 hours
    private final long REFRESH_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7; // 7 days

    public JwtUtil() {
        // Decode the base64 secret key and create a SecretKey object
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY_STRING);
        this.SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role"));
    }

    public Long extractBaseId(String token) {
        Object baseId = extractClaim(token, claims -> claims.get("baseId"));
        return baseId == null ? null : Long.valueOf(baseId.toString());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Add custom claims if userDetails is an instance of org.springframework.security.core.userdetails.User
        if (userDetails instanceof org.springframework.security.core.userdetails.User user) {
            // Extract role from authorities
            String role = user.getAuthorities().stream().findFirst().map(a -> a.getAuthority().replace("ROLE_", "")).orElse("");
            claims.put("role", role);
            // baseId is not available in UserDetails, so skip unless you have a custom UserDetails implementation
        }
        return createToken(claims, userDetails.getUsername());
    }

    public String generateTokenWithClaims(UserDetails userDetails, String role, Long baseId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("baseId", baseId);
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_MS))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final String type = extractClaim(token, claims -> (String) claims.get("type"));
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && "refresh".equals(type));
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractUsername(token);
    }
} 