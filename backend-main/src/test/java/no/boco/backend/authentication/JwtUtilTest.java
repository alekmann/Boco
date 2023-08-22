package no.boco.backend.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import no.boco.backend.authentication.jwt.JwtUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {
    @Autowired
    JwtUtil jwtUtil;

    @Test
    @DisplayName("Create access token")
    public void createAccessToken() {
        String accessToken = jwtUtil.createAccessToken("Roger");
        assertFalse(accessToken.isBlank());
        assertTrue(accessToken.length() > 20);
    }

    @Test
    @DisplayName("Create refresh token")
    public void createRefreshToken() {
        String accessToken = jwtUtil.createRefreshToken("Roger");
        assertFalse(accessToken.isBlank());
        assertTrue(accessToken.length() > 20);
    }

    @Test
    @DisplayName("Decode access token")
    public void decodeAccessToken() {
        // Pre-generated token with no expiration
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuaWNvbGF5ckBzdHVkLm50bnUubm8ifQ.dPJut7TxkL9liy_pq-Evyz_oxRzmnuDKkDKBZj49FOU";

        String subject = jwtUtil.decode(accessToken);
        assertEquals("nicolayr@stud.ntnu.no", subject);
    }

    @Test
    @DisplayName("Expired tokens are deemed invalid")
    public void expiredTokensAreInvalid() {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuaWNyb25lc3NAZ21haWwuY29tIiwiZXhwIjoxNjUxMjI2Mjk4fQ.dxBktwQfODcNZdMSCD1330F3_LrjspvfOOAIH1awGPA";
        Exception exception = assertThrows(JwtException.class, () -> {
            jwtUtil.decode(expiredToken);
        });
        assertEquals(ExpiredJwtException.class, exception.getClass());
    }


}
