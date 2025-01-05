package com.choco_tur.choco_tur.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-token-expiration-time-ms}")
    private long jwtAccessTokenExpirationMs;

    @Value("${security.jwt.refresh-token-expiration-time-ms}")
    private long jwtRefreshTokenExpirationMs;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(String email) {
        return generateAccessToken(new HashMap<>(), email, jwtAccessTokenExpirationMs);
    }

    public String generateRefreshToken(String email) {
        return generateAccessToken(new HashMap<>(), email, jwtRefreshTokenExpirationMs);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, String email, long expirationTimeMs) {
        return buildToken(extraClaims, email, expirationTimeMs);
    }

    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email)) && !isTokenExpired(token);
    }

    public boolean isTokenValid(String token, String email, String businessId) {
        final Claims claims = extractAllClaims(token);
        final String extractedEmail = claims.getSubject();
        final String extractedBusinessId = claims.get("businessId", String.class);
        return (extractedEmail.equals(email)) && (extractedBusinessId.equals(businessId)) && !isTokenExpired(token);
    }

    public long getAccessTokenExpirationTime() {
        return jwtAccessTokenExpirationMs;
    }
    public long getRefreshTokenExpirationTime() { return jwtRefreshTokenExpirationMs; }

    private String buildToken(
            Map<String, Object> extraClaims,
            String email,
            long expirationMs
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        // TODO: Refresh token??
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
