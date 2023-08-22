package no.boco.backend.user;

import no.boco.backend.authentication.PasswordRequest;
import no.boco.backend.authentication.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@CrossOrigin
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Signs up a user with the given data
     * @param registrationRequest user data
     * @return The email of the user if the signup is successful, an error message if it wasn't
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest registrationRequest) {
        try {
            logger.trace("Attempting to register user...");
            return new ResponseEntity<>(userService.register(registrationRequest), HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e){
            logger.trace(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Endpoint for accessing information about a user
     * @return data about the user
     */
    @GetMapping("/me")
    public ResponseEntity<User> getUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Updates the given values for the user
     * @param newUserData new values
     * @param authentication caller of the method. This is the users that will change
     */
    @PutMapping("/me")
    public ResponseEntity<HttpStatus> updateCurrentUser(@RequestBody User newUserData, Authentication authentication) {
        try {
            logger.trace("Attempting to update user");
            User caller = (User) authentication.getPrincipal();
            userService.updateUser(newUserData, caller);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tries to change the password of the user with the given password
     * @param passwordRequest old and new password for changing
     * @return HttpStatus and an error message if there is one
     */
    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordRequest passwordRequest, Authentication authentication) {
        try {
            logger.trace("Attempting to update user");
            User user = (User) authentication.getPrincipal();
            userService.changePassword(user, passwordRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.trace(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Activates a user by passing the activation code.
     * @param activationCode activation code from email
     * @return HTTP OK
     */
    @PostMapping("/activate/{activationCode}")
    public ResponseEntity<String> activateUser(@PathVariable String activationCode){
        try{
            logger.trace("Attempting to activate user " + activationCode);
            userService.activateUser(activationCode);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(NoSuchElementException e){
            logger.trace(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Deletes the user that is logged in. Checks if you are verified as the user.
     * @param authentication security context of the user
     * @return HTTP OK
     */
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(Authentication authentication) {
        logger.trace("Deleting user" + authentication.getName());
        User user = (User) authentication.getPrincipal();

        try {
            userService.deleteUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Fetches a user with given user id.
     * @param id user id
     * @return user
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.trace("Getting user: " + id);
        try{
            return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
