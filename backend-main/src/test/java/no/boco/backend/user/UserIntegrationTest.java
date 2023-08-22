package no.boco.backend.user;

import no.boco.backend.authentication.PasswordRequest;
import no.boco.backend.authentication.RegistrationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserIntegrationTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void emptyDatabase() {
        userRepository.deleteAll();
    }

    @Nested
    class changeUser{
        @Test
        public void change_user_profile_picture() {
            //Save a new user
            User user = new User("johan@normann.no", "abc", "Johan", "Normann", null,
                    Date.valueOf("2001-01-01"), null);
            user = userRepository.save(user);

            String picture = "\"me\"";
            userService.changePicture(user, picture);

            assertTrue(user.getProfilePicture().equals("me"));
        }

        @Test
        public void change_name_test(){
            User user = new User();
            userRepository.save(user);
            userService.changeName("navn navnesen", user);

            assertEquals(user.getFirstName() + " " + user.getLastName(), " navn navnesen");
        }

        @Test
        public void update_user_endpoint() {
            User user = new User();
            user.setEmail("bob@bob.no");
            userRepository.save(user);

            user.setProfilePicture("\"me\"");
            user.setFirstName("Navn,Navnesen");

            userService.updateUser(user, user);

            assertEquals("Navn ", userRepository.findByEmail("bob@bob.no").get().getFirstName());
            assertEquals("Navnesen", userRepository.findByEmail("bob@bob.no").get().getLastName());

        }
    }

    @Nested
    class handleRegistrationRequest{
        @Test
        public void saves_new_user(){
            assertTrue(userRepository.findAll().isEmpty());
            RegistrationRequest request = new RegistrationRequest("test@test.no","abc","Test","Testesen",
                    null,Date.valueOf("2000-01-01"));

            //Save the new user
            User user = userService.handleRegistrationRequest(request);
            assertTrue(userRepository.existsByEmail(request.getEmail()));

            //Verify that the password gets encrypted
            assertNotEquals(user.getPassword(), request.getPassword());
        }

        @Test
        public void handles_duplicate_user(){
            //Save a new user
            User user = new User("johan@normann.no", "abc", "Johan", "Normann", null,
                    Date.valueOf("2001-01-01"), null);
            user = userRepository.save(user);

            //Try to register again
            RegistrationRequest request = new RegistrationRequest();
            request.setEmail(user.getEmail());
            request.setPassword(user.getPassword());
            request.setBirthDate(user.getBirthDate());
            request.setLocation(user.getLocation());
            request.setFirstName(user.getFirstName());
            request.setLastName(user.getLastName());

            try{
                userService.handleRegistrationRequest(request);
                fail();
            }catch(UserAlreadyExistsException e){
                //pass test
            }
        }
    }

    @Test
    public void generate_activation_does_not_throw(){
        try {
            String code = userService.generateActivationCode();
            assertFalse(code.isEmpty());
        }catch(Exception e){
            fail();
        }
    }



    @Nested
    class loadUserByUsername{
        @Test
        public void handles_match(){
            User user = new User("ola@normann.no","abc","Ola","Normann",null,Date.valueOf("2000-01-01"),null);
            userRepository.save(user);

            try {
                UserDetails userDetails = userService.loadUserByUsername("ola@normann.no");
            }catch(UsernameNotFoundException e){
                fail();
            }
        }

        @Test
        public void throws_error_at_miss(){
            try {
                UserDetails userDetails = userService.loadUserByUsername("ola@normann.no");
                fail();
            }catch(UsernameNotFoundException e){
                //pass test
            }
        }
    }

    @Nested
    class changePassword{
        @Test
        public void method_changes_password(){
            User user = new User("ola@normann.no","abc","Ola","Normann",null,Date.valueOf("2000-01-01"),null);
            PasswordEncoder encoder = userService.getbCrypt();
            user.setPassword(encoder.encode(user.getPassword()));
            user = userRepository.save(user);

            //Ensure 'abc' is the password
            assertTrue(encoder.matches("abc", user.getPassword()));

            //Change to 'password'
            PasswordRequest request = new PasswordRequest("abc","password","password");
            userService.changePassword(user, request);

            //Ensure the new password is 'password'
            user = userRepository.findAll().get(0);
            assertTrue(encoder.matches("password", user.getPassword()));
        }

        @Test
        public void handles_invalid_new_password(){
            User user = new User("ola@normann.no","abc","Ola","Normann",null,Date.valueOf("2000-01-01"),null);
            user = userRepository.save(user);

            PasswordRequest request = new PasswordRequest("abc","wrong","password");
            try {
                userService.changePassword(user, request);
                fail();
            }catch(IllegalArgumentException e){
                assertEquals(e.getMessage(),"Passwords do not match");
            }
        }

        @Test
        public void handles_invalid_old_password(){
            User user = new User("ola@normann.no","abc","Ola","Normann",null,Date.valueOf("2000-01-01"),null);
            PasswordEncoder encoder = userService.getbCrypt();
            user.setPassword(encoder.encode(user.getPassword()));
            user = userRepository.save(user);

            PasswordRequest request = new PasswordRequest("wrong","password","password");
            try{
                userService.changePassword(user, request);
                fail();
            }catch(IllegalArgumentException e){
                assertEquals(e.getMessage(),"Old password is incorrect");
            }
        }
    }

    @Nested
    class activateUser{
        @Test
        public void method_activates_user(){
            User user = new User("ola@normann.no","abc","Ola","Normann",null,Date.valueOf("2000-01-01"), Role.USER);
            user.setActivation("strvhr");
            user = userRepository.save(user);

            assertFalse(user.isEnabled());
            userService.activateUser(user.getActivation());
            user = userRepository.findAll().get(0);
            assertTrue(user.isEnabled());
        }

        @Test
        public void throws_error_at_wrong_user(){
            assertTrue(userRepository.findAll().isEmpty());

            try {
                userService.activateUser("hh");
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }
    }
}
