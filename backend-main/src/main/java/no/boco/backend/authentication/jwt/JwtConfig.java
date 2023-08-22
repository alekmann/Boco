package no.boco.backend.authentication.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Configurations used for creating and decoding JWTs. The actual creating and decoding is handled in the
 * JwtUtils class
 */
@Configuration
public class JwtConfig {

    private static final String SECRET_KEY = "gU5v9I1harU9wUSgx9PvtY/xSCrSdCUaMGReamgRBjk=";

    // Mainly used for code readability
    private static final long MINUTES = 60 * 1000;
    private static final long DAYS = 24 * 60 * MINUTES;

    // Some constants
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 15 * MINUTES;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 14 * DAYS;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh-token";

    public Date accessTokenExpirationDate() {
        return new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public Date refreshTokenExpirationDate() {
        return new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);
    }

    public String getTokenPrefix() {
        return TOKEN_PREFIX;
    }

    public String getCookieName() {
        return REFRESH_TOKEN_COOKIE_NAME;
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

}


