package no.boco.backend.user;

import no.boco.backend.authentication.PasswordRequest;
import no.boco.backend.authentication.RegistrationRequest;
import no.boco.backend.organization.Organization;
import no.boco.backend.organization.OrganizationService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * Service class for users
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder bCrypt;
    private final MailService mailService;
    private final OrganizationService organizationService;

    public UserService(UserRepository userRepository, PasswordEncoder bCrypt, MailService mailService, OrganizationService organizationService) {
        this.userRepository = userRepository;
        this.bCrypt = bCrypt;
        this.mailService = mailService;
        this.organizationService = organizationService;
    }

    /**
     * Tries to find a user based on their username (email)
     * @param username username of user (email)
     * @return user, if there is one
     * @throws UsernameNotFoundException if there is no user with the given username (email)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    /**
     * Tries to register the user to the database, if the email is available
     *
     * @param registrationRequest registration information
     * @return User that was registered
     */
    public String register(RegistrationRequest registrationRequest){
        User user = handleRegistrationRequest(registrationRequest);
        mailService.sendActivationEmail(user);

        return user.getUsername();
    }

    /**
     * Tries to register the user to the database, if the email is available
     *
     * @param registrationRequest registration information
     * @return User that was registered
     */
    User handleRegistrationRequest(RegistrationRequest registrationRequest) {
        if(userExists(registrationRequest.getEmail())) throw new UserAlreadyExistsException("Email is taken");

        // TODO: Add more data validation

        // Encode password
        String encodedPassword = bCrypt.encode(registrationRequest.getPassword());

        // Parse date

        User user = new User(
                registrationRequest.getEmail(),
                encodedPassword,
                registrationRequest.getFirstName(),
                registrationRequest.getLastName(),
                registrationRequest.getLocation(),
                registrationRequest.getBirthDate(),
                Role.USER
        );

        user.setActivation(generateActivationCode());
        return userRepository.save(user);
    }

    /**
     * Check if the user already exists in the database
     *
     * @param username username to check (email in this case)
     * @return true if user exists, false if not
     */
    private boolean userExists(String username) {
        return userRepository.existsByEmail(username);
    }

    /**
     * Check if the user already exists in the database
     * @param userId the UserId to check.
     * @return true if user exists, false if not.
     */
    public boolean userExists(Long userId){
        return userRepository.existsById(userId);
    }

    /**
     * Changes the password of the user, given the data is valid
     *
     * @param caller caller of method
     * @param passwordRequest new password data
     */
    public void changePassword(User caller, PasswordRequest passwordRequest) {
        User existingUser = (User) loadUserByUsername(caller.getEmail());
        if(!passwordRequest.isNewPasswordValid()) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if(!bCrypt.matches(passwordRequest.getOldPassword(), existingUser.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        String encodedNewPassword = bCrypt.encode(passwordRequest.getNewPassword());
        existingUser.setPassword(encodedNewPassword);
        userRepository.save(existingUser);
    }

    /**
     * Activate a user account, changing its enabled property to true.
     * @param activation the Activation code sent by e-mail.
     * @throws NoSuchElementException if a User with the given Activation code is not found.
     */
    public void activateUser(String activation) throws NoSuchElementException{
        Optional<User> userOpt = userRepository.findByActivation(activation);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            user.setActivation(null);
            userRepository.save(user);
        }else{
            throw new NoSuchElementException("Could not find user with the given Activation code");
        }
    }

    /**
     * Generate a random alphanumerical String of 10 characters
     * @return a random string.
     */
    String generateActivationCode(){
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    //Used in test class
    public PasswordEncoder getbCrypt() {
        return bCrypt;
    }

    //Changes first and last name of user, deletes quote marks that are not supposed to be there.
    public void changeName(String newName, User user) throws IllegalArgumentException {
        String[] nameArray = newName.split(",");
        StringBuilder firstName = new StringBuilder();
        for(int i = 0; i < nameArray.length-1; i++) {
            firstName.append(nameArray[i]).append(" ");
        }
        String lastName = nameArray[nameArray.length-1];

        String firstNameString = firstName.toString().replace("\"", "");
        String lastNameString = lastName.replace("\"", "");


        user.setFirstName(firstNameString);
        user.setLastName(lastNameString);

        userRepository.save(user);
    }

    /**
     * Changes profile picture
     * @param user
     * @param picture
     */
    public void changePicture(User user, String picture) {
        user.setProfilePicture(picture.replace("\"", ""));
        userRepository.save(user);
    }

    /**
     * Deletes a user. If the user is the only employee in an organization, the organization is also deleted.
     * @param user User to be deleted
     * @throws NoSuchElementException if the user is not found
     */
    public void deleteUser(User user) throws NoSuchElementException {
        try {
            Set<Organization> organizations = user.getOrganizations();
            for(Organization organization : organizations){
                if(organization.getEmployees().size() == 1){
                    organizationService.deleteOrganization(organization.getOrgNum(), user);
                }
            }
            userRepository.delete(user);
        } catch (Exception e) {
            throw new NoSuchElementException("Fant ikke brukeren");
        }
    }

    /**
     *
     *
     * @param id
     * @return a user object with all information.
     */
    public User getUserById(Long id){
        if(userExists(id)){
            return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        }
        return null;
    }

    /**
     * Updates the user with the appropriate methods with the given data
     * @param newUserData newUserData
     * @param caller user to change
     *
     */
    public void updateUser(User newUserData, User caller) {
        // Full name is sent here because of previously made logic
        if(newUserData.getFirstName() != null) {
            changeName(newUserData.getFirstName(), caller);
        }
        if(newUserData.getProfilePicture() != null) {
            changePicture(caller, newUserData.getProfilePicture());
        }

    }
}
