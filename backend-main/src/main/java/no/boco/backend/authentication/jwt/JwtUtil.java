package no.boco.backend.authentication.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Abstracts the creation and decoding of JWTs.
 */
@Configuration
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Creates access token with the configs defined in JwtConfig and adds the necessary prefix to it
     * @param subject subject of jwt, in this case an email address
     * @return jwt as string with given configurations
     */
    public String createAccessToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(jwtConfig.accessTokenExpirationDate())
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    /**
     * Creates a refresh token for the given subject with the configurations given from JwtConfig
     * @param subject subject of jwt, in this case an email address
     * @return jwt as string with the given configurations
     */
    public String createRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(jwtConfig.refreshTokenExpirationDate())
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    /**
     * Decodes a jwt with. If the token is expired, has been tampered with or is otherwise not trustworthy,
     * the method will throw a JwtException.
     * @param jwt Jwt
     * @return Subject from the payload of the jwt. In this case it should be the email of the authenticated user
     * @throws JwtException this exception is thrown if the jwt is not trustable.
     */
    public String decode(String jwt) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getSecretKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    /**
     * Returns name of cookie used for the refresh token
     *
     * @return name of cookie
     */
    public String cookieName() {
        return jwtConfig.getCookieName();
    }

}

