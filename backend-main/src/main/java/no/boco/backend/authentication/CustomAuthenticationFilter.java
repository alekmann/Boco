package no.boco.backend.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.boco.backend.authentication.jwt.JwtResponse;
import no.boco.backend.authentication.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private JwtUtil jwtUtil;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
    }

    /**
     * When a user tries to login, this method will be called. It will try to login a with the given credentials,
     * and if the credentials are valid, the #successfullAuthentication method is called
     *
     * @param request http request
     * @param response http response
     * @return Authentication
     * @throws AuthenticationException if the login there is an error with the login attemt or the given credentials
     *          were invalid
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            logger.info("Attempting authentication...");
            // Reading the user data from the request body
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            // Trying to authenticate the user
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );
            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            logger.error("Authentication failed... Invalid format");
            throw new CustomAuthenticationException("Invalid format");
        } catch (AuthenticationException e) {
            logger.error("Authentication failed... Invalid credentials");
            throw new CustomAuthenticationException("Invalid credentials");
        }
    }

    /**
     * If an authentication attempts is successful, this method is called. In other words, we have a user, and we
     * must now generate them an access token they can use to make authentication requests.
     *
     * @param request http request
     * @param response http response
     * @param chain filter chain
     * @param authResult result of authentication
     * @throws IOException if
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        logger.info("Authentication successful");
        // Creating access token and add it to the authorization header
        String accessToken = jwtUtil.createAccessToken(authResult.getName());

        // We also have to make a refresh token for refreshing access tokens
        String refreshToken = jwtUtil.createRefreshToken(authResult.getName());

        Cookie cookie = new Cookie(jwtUtil.cookieName(), refreshToken);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        // Sending the token as a response to the login
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(new JwtResponse(accessToken)));
    }


}
