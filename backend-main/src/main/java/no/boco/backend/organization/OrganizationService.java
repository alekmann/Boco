package no.boco.backend.organization;

import no.boco.backend.user.User;
import no.boco.backend.userorg.OrganizationRole;
import no.boco.backend.userorg.UserOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    /**
     * Changes organization picture
     * @param org
     * @param picture
     */
    public void changePicture(Organization org, String picture) {
        org.setProfilePicture(picture.replace("\"", ""));
        organizationRepository.save(org);
    }

    /**
     * Save a new Organization to the database.
     * @param organization the new Organization.
     * @return the saved Organization object.
     * @throws IllegalArgumentException if the Organization already exists
     */
    public Organization add(Organization organization) throws IllegalArgumentException{
        if(organizationExists(organization.getOrgNum())){
            throw new IllegalArgumentException("This organization already exists");
        }
        return organizationRepository.save(organization);
    }

    /**
     * Check if the given organization exists.
     * @param orgNum the orgNum of the Organization.
     * @return true or false.
     */
    public boolean organizationExists(Long orgNum) {
        return organizationRepository.existsById(orgNum);
    }

    /**
     * Edit an existing Organization.
     * @param newOrg the orgNum needs to match an existing Organizations orgNum, the other fields gets changed.
     * @param user the User calling this method.
     * @throws IllegalArgumentException if the User calling is not an Admin in the Organization.
     * @throws NoSuchElementException if the Organization does not exist from before.
     */
    public void editOrganization(Organization newOrg, User user) throws IllegalArgumentException, NoSuchElementException {
        //Check if Organization exists
        if(newOrg.getOrgNum() == null) throw new NoSuchElementException("Organization must not be null");
        Optional<Organization> orgOpt = organizationRepository.findById(newOrg.getOrgNum());
        if(orgOpt.isEmpty()) {
            throw new NoSuchElementException("Could not find organization with the given Id");
        }

        // Check if user is admin in organization
        Organization oldOrg = orgOpt.get();
        if(!isAdmin(user, oldOrg)){
            throw new IllegalArgumentException("Can not edit organization if not admin");
        }

        oldOrg.setNickname(newOrg.getNickname());
        organizationRepository.save(oldOrg);
    }

    /**
     * Makes a request to broennoeysundsregistrene to get data about an organization with a given organization number.
     * @param orgNum organization number
     * @return name of the organization
     */
    public String getOrgNameByOrgNum(Long orgNum){
        // URL of the request
        String url = "https://data.brreg.no/enhetsregisteret/api/enheter/" + orgNum;
        // Using Spring 5 Web Client
        WebClient webClient = WebClient.create();
        WebClient.ResponseSpec responseSpec = webClient.get()
                .uri(url)
                .retrieve();
        // Reading the response into a mono
        String responseBody = responseSpec.bodyToMono(String.class).block();
        if(responseBody != null){
            // Finding the name of the org in the data
            String nameField = responseBody.split(",")[1];
            return nameField.substring(8, nameField.length()-1);
        }
        return "Fant ingen organisasjon med dette organisasjonsnummeret!";
    }

    /**
     * Find an Organization by its orgNum.
     * @param orgNum the orgNum.
     * @return the Organization object found.
     * @throws NoSuchElementException if no Organization with the given orgNum exists.
     */
    public Organization getOrganization(Long orgNum) throws NoSuchElementException{
        Optional<Organization> orgOpt = organizationRepository.findById(orgNum);
        if(orgOpt.isEmpty()) throw new NoSuchElementException("Could not find an Organization by the given orgNum");
        return orgOpt.get();
    }

    /**
     * Get all employees for an Organization.
     * @param orgNum the orgNum of the Organization.
     * @param caller the User calling this method.
     * @return the Set of all UserOrganization objects found.
     * @throws IllegalArgumentException if the caller is not an Employee in the given Organization.
     * @throws NoSuchElementException if the Organization does not exist.
     */
    public Set<UserOrganization> getEmployees(Long orgNum, User caller) throws IllegalArgumentException, NoSuchElementException{
        Optional<Organization> orgOpt = organizationRepository.findById(orgNum);
        if(orgOpt.isEmpty()) throw new NoSuchElementException("Could not find organization with the given Id");
        Organization org = orgOpt.get();

        //Check if user is an employee
        Set<UserOrganization> userOrgs = org.getUserOrganizations();
        for(UserOrganization userOrg : userOrgs){
            if(userOrg.getUser().equals(caller)){
                return userOrgs;
            }
        }

        throw new IllegalArgumentException("User is not in the Organization");
    }

    /**
     * Check if the given user is an Admin in the Organization.
     * @param user the User to check.
     * @param organization the Organization to check.
     * @return true or false.
     */
    private boolean isAdmin(User user, Organization organization){
        Set<UserOrganization> userOrganizations = organization.getUserOrganizations();
        for(UserOrganization userOrganization : userOrganizations){
            if(userOrganization.getUser().equals(user) && userOrganization.getOrganizationRole().equals(OrganizationRole.ADMIN)){
                return true;
            }
        }
        return false;
    }

    /**
     * Delete an organization from the database.
     * @param orgNum the orgNum of the Organization.
     * @param caller the Caller of this method.
     * @return true if succesfull, false if not.
     */
    public Boolean deleteOrganization(Long orgNum, User caller){
        try{
            Organization organization = getOrganization(orgNum);
            if(isAdmin(caller, organization)){
                organizationRepository.delete(organization);
                return true;
            }else {
                return false;
            }
        } catch (NoSuchElementException e){
            logger.trace(e.getMessage());
            return false;
        }
    }
}
