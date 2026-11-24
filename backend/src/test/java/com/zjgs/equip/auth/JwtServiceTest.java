package com.zjgs.equip.auth;

import com.zjgs.equip.common.Role;
import com.zjgs.equip.config.JwtProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    private JwtService service() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-for-equipment-management-jwt-2024");
        properties.setExpirationSeconds(3600);
        return new JwtService(properties);
    }

    @Test
    void createsAndParsesToken() {
        JwtService jwtService = service();
        UserPrincipal principal = new UserPrincipal(2023000001L, "2023000001", "王亦格",
                Role.STUDENT, 2023000001L);

        String token = jwtService.createToken(principal);
        Claims claims = jwtService.parse(token);
        UserPrincipal parsed = jwtService.toPrincipal(claims);

        assertThat(parsed.userId()).isEqualTo(2023000001L);
        assertThat(parsed.role()).isEqualTo(Role.STUDENT);
        assertThat(parsed.stuId()).isEqualTo(2023000001L);
        assertThat(parsed.name()).isEqualTo("王亦格");
        assertThat(claims.getId()).isNotBlank();
    }
}
