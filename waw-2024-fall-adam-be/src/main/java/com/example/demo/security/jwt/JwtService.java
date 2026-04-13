package com.example.demo.security.jwt;

import com.example.demo.config.AuthConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public class JwtService {
    private final AuthConfigProperties authConfigProperties;


    public JwtService(AuthConfigProperties authConfigProperties) {
        this.authConfigProperties = authConfigProperties;
    }

    //   token is valid 60 min
    public String generateToken(Map<String, Object> extraClaims, String email) {
        Date now = Date.from(Instant.now());
        Date expiration = Date.from(Instant.now().plus(authConfigProperties.validity()));

        return Jwts.builder().claims(extraClaims)
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getKey())
                .compact();
    }

    //   refresh token is valid 7 days
    public String generateRefreshToken(String email) {
        Date now = Date.from(Instant.now());
        Date expiration = Date.from(Instant.now()
                .plus(authConfigProperties.validity().multipliedBy(7 * 24)));

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getKey())
                .compact();
    }

    public String getUserEmailFromToken(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = getClaims(token);
        return claimResolver.apply(claims);
    }

    public boolean validateToken(String jwtToken, String springUserName) {
        String jwtUserEmail = getUserEmailFromToken(jwtToken);
        boolean isExpired = getExpirationFromToken(jwtToken).before(new Date());
        return !isExpired && jwtUserEmail.equals(springUserName);
    }

    public Date getExpirationFromToken(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    private Claims getClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(authConfigProperties.secret().getBytes());
    }

}
