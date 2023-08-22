package no.boco.backend.authentication;

import io.jsonwebtoken.JwtException;
import no.boco.backend.authentication.jwt.JwtResponse;
import no.boco.backend.authentication.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Used for refreshing access tokens. The refresh token cookie is checked, and if it contains a valid refresh token
     * a new access token is created for the subject in the refresh token and the token is sent back. The refresh
     * token is also refreshed, effectively extending its expiration date for further usage
     *
     * @param request http request
     * @param response http response
     * @return access token if refresh token is valid, blank if not
     */
    @PostMapping("/token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = "";

        if (request.getCookies() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(jwtUtil.cookieName())) {
                refreshToken = c.getValue();
            }
        }

        if (refreshToken.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // If we get here, we should have a refresh token. Now we must validate it
        try {
            String subject = jwtUtil.decode(refreshToken);

            // If we get here, the token has been validated

            // We create a new access token for the user
            String accessToken = jwtUtil.createAccessToken(subject);

            // We also create a new refresh token in a http-only cookie so the user can fetch new access tokens

            // when needed
            Cookie refreshTokenCookie = new Cookie(jwtUtil.cookieName(), jwtUtil.createRefreshToken(subject));
            refreshTokenCookie.setHttpOnly(true);

            response.addCookie(refreshTokenCookie);
            return new ResponseEntity<>(new JwtResponse(accessToken), HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }
}
