package no.boco.backend.userorg;

import no.boco.backend.organization.Organization;
import no.boco.backend.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping
public class UserOrganizationController {
    Logger logger = LoggerFactory.getLogger(UserOrganizationController.class);

    @Autowired
    private UserOrganizationService userOrganizationService;

    /**
     * Adds an employee to an organization and saves it to the database
     * @param request request containing email, org number and role
     * @param authentication security context of the requesting user
     * @return object containing the new user organization relation
     */
    @PostMapping("/employees")
    public ResponseEntity<UserOrganization> addEmployee(@RequestBody EmployeeRequest request, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        logger.trace("Adding employee, request: " + request);
        try{
            return new ResponseEntity<>(userOrganizationService.addEmployee(request, user), HttpStatus.CREATED);
        }catch(NoSuchElementException | IllegalArgumentException | UsernameNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Creates an organization
     * @param organization organization to be saved to the database
     * @param authentication security context of the user creating the organization
     * @return the saved organization
     */
    @PostMapping("/organizations")
    public ResponseEntity<Organization> createOrganization(@RequestBody Organization organization, Authentication authentication) {
        logger.trace("Adding organization " + organization);
        try {
            User user = (User) authentication.getPrincipal();
            return new ResponseEntity<>(userOrganizationService.createOrganization(organization, user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Edits the role af an employee. Requesting user has to be admin of the organization.
     * @param request Request containing email, org number and role
     * @param authentication Security context of the requesting user
     * @return Edited entry in user organization
     */
    @PutMapping("/employees")
    public ResponseEntity<UserOrganization> editEmployee(@RequestBody EmployeeRequest request, Authentication authentication){
        logger.trace("Editing employee, request " + request);
        User user = (User) authentication.getPrincipal();
        try{
            return new ResponseEntity<>(userOrganizationService.editEmployee(request, user), HttpStatus.CREATED);
        }catch(NoSuchElementException | IllegalArgumentException | UsernameNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Search for employees in an Organization by their full name.
     * @param orgNum the orgNum of the Organization.
     * @param queryParams search paramters. Supported right now are 'search'.
     * @param authentication Security context of the requesting user
     * @return all matches.
     */
    @GetMapping("organizations/{orgNum}/employees/search")
    public ResponseEntity<List<UserOrganization>> searchEmployees(@PathVariable Long orgNum, @RequestParam Map<String, String> queryParams, Authentication authentication){
        logger.trace("Searching employees");
        User user = (User) authentication.getPrincipal();
        try{
            String name = queryParams.get("search");
            return new ResponseEntity<>(userOrganizationService.searchEmployees(orgNum, name, user), HttpStatus.CREATED);
        }catch(NoSuchElementException | IllegalArgumentException | ClassCastException | NullPointerException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes a user from an organization by deleting the row of the relation in the UserOrganization table.
     * The caller has to be the admin of the organization.
     * @param orgNum organization number of the organization
     * @param userId user id of the employee
     * @param authentication security context of the caller
     * @return HTTP OK
     */
    @DeleteMapping("/organizations/{orgNum}/employees/{userId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long orgNum, @PathVariable Long userId, Authentication authentication){
        logger.trace("Deleting employee " + orgNum + ", " + userId);
        User user = (User) authentication.getPrincipal();
        try{
            userOrganizationService.deleteEmployee(orgNum, userId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
