package com.zjgs.equip.auth;

import com.zjgs.equip.common.Role;
import com.zjgs.equip.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(UserPrincipal principal) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getExpirationSeconds());
        return Jwts.builder()
                .subject(String.valueOf(principal.userId()))
                .id(UUID.randomUUID().toString())
                .claim("username", principal.username())
                .claim("name", principal.name())
                .claim("role", principal.role().name())
                .claim("stuId", principal.stuId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public UserPrincipal toPrincipal(Claims claims) {
        Object stuId = claims.get("stuId");
        return new UserPrincipal(
                Long.valueOf(claims.getSubject()),
                claims.get("username", String.class),
                claims.get("name", String.class),
                Role.valueOf(claims.get("role", String.class)),
                stuId == null ? null : Long.valueOf(String.valueOf(stuId))
        );
    }

    public long getExpirationSeconds() {
        return properties.getExpirationSeconds();
    }
}
