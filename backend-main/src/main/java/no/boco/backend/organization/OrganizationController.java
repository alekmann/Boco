package no.boco.backend.organization;

import no.boco.backend.user.User;
import no.boco.backend.userorg.UserOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @Autowired private OrganizationService organizationService;

    Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    /**
     * Edits an organization
     * @param organization organization to be edited
     * @param authentication security context of the user editing the organization
     * @return HTTP OK
     */
    @PutMapping
    public ResponseEntity<String> editOrganization(@RequestBody Organization organization, Authentication authentication) {
        logger.trace("Editing organization " + organization);
        User user = (User) authentication.getPrincipal();

        try {
            organizationService.editOrganization(organization, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Fetches an organization with the given organization number
     * @param orgNum organization number
     * @return organization and HTTP status
     */
    @GetMapping("/{orgNum}")
    public ResponseEntity<Organization> getOrganization(@PathVariable Long orgNum){
        try{
            return new ResponseEntity<>(organizationService.getOrganization(orgNum), HttpStatus.OK);
        }catch(NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gets the organization name of the organization with the given organization number.
     * @param orgNum organization number
     * @return Name of the organization
     */
    @GetMapping("/{orgNum}/name")
    public ResponseEntity<String> getOrgNameByOrgNum(@PathVariable Long orgNum) {
        try{
            return new ResponseEntity<>(organizationService.getOrgNameByOrgNum(orgNum), HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes an organization.
     * @param orgNum organization number.
     * @param authentication security context of the user calling this
     * @return boolean indication if the deletion was successful
     */
    @DeleteMapping("/{orgNum}")
    public ResponseEntity<Boolean> deleteOrganization(@PathVariable Long orgNum, Authentication authentication){
        User user = (User) authentication.getPrincipal();
            try{
            return new ResponseEntity<>(organizationService.deleteOrganization(orgNum, user), HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Fetch the employees of an organization
     * @param orgNum organization number
     * @param authentication security context of the requesting user
     * @return list of employees of the organization
     */
    @GetMapping("/{orgNum}/employees")
    public ResponseEntity<Set<UserOrganization>> getEmployees(@PathVariable Long orgNum, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        try{
            return new ResponseEntity<>(organizationService.getEmployees(orgNum, user), HttpStatus.OK);
        }catch(NoSuchElementException | IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Changes the profile picture of an organization.
     * @param orgNum organization number
     * @param picture new profile picture
     * @return HTTP OK
     */
    @PatchMapping("/{orgNum}/picture")
    public ResponseEntity<String> changePicture(@PathVariable Long orgNum, @RequestBody String picture) {
        logger.trace("Changing picture for " + orgNum);

        try {
            organizationService.changePicture(organizationService.getOrganization(orgNum), picture);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
