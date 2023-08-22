package no.boco.backend.user;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception for throwing when a user already exists
 */
public class UserAlreadyExistsException extends AuthenticationException {

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
