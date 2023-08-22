package no.boco.backend.authentication.jwt;


import io.jsonwebtoken.JwtException;
import no.boco.backend.user.User;
import no.boco.backend.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtValidationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/login") || request.getServletPath().equals("/token") || request.getServletPath().equals("/logout") || request.getServletPath().equals("/register")){
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token from request
        String token = authorizationHeader.replace("Bearer ", "");

        // Try to validate token
        try {
            String subject = jwtUtil.decode(token);

            User user = (User) userService.loadUserByUsername(subject);

            // If we reach this part of the code, the jwt is deemed valid
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            // Set the user as authenticated, then keep going
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            logger.error("Token {} cannot be trusted", token);
            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            logger.error(e.getMessage());
            filterChain.doFilter(request, response);
        }

    }
}


