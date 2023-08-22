package no.boco.backend.userorg;

import no.boco.backend.organization.Organization;
import no.boco.backend.organization.OrganizationService;
import no.boco.backend.user.User;
import no.boco.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for the UserOrganization table. This class handles all interaction between OrganizationService
 * and UserService to avoid circular dependencies.
 */
@Service
public class UserOrganizationService {
    @Autowired
    private UserOrganizationRepository userOrgRepository;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UserService userService;

    /**
     * Create a new Organization and set the caller as Admin.
     * @see Organization
     * @param organization the new Organization.
     * @param creator the caller of this method.
     * @return the saved Organization object.
     * @throws IllegalArgumentException if the Organization already exists.
     */
    public Organization createOrganization(Organization organization, User creator) throws IllegalArgumentException {
        organization.setOrgName(organizationService.getOrgNameByOrgNum(organization.getOrgNum()));
        UserOrganization userOrganization = new UserOrganization(creator, organization, OrganizationRole.ADMIN);
        Organization result = organizationService.add(organization);
        userOrgRepository.save(userOrganization);
        return result;
    }

    /**
     * Search for employees in an Organization by their names.
     * @param orgNum the orgNum of the Organization.
     * @param name the search term, will filter the Users full name
     * @param caller the User calling this method.
     * @return a Set of all matches.
     * @throws NoSuchElementException
     * @throws IllegalArgumentException
     */
    public List<UserOrganization> searchEmployees(Long orgNum, String name, User caller) throws NoSuchElementException, IllegalArgumentException{
        //This method is called to ensure the caller is an employee in the Organization.
        Set<UserOrganization> allEmployees = organizationService.getOrganization(orgNum).getUserOrganizations();
        for(UserOrganization userOrganization : allEmployees){
            if(userOrganization.getUser().equals(caller)){
                return userOrgRepository.searchByOrgAndFullName(orgNum, name.toLowerCase());
            }
        }
        throw new IllegalArgumentException("Can not search if not an employee in the Organization");
    }

    /**
     * Edit the Role of an employee. Must be called by an Admin in the organization.
     * @param request the Request with existing User, Organization, and the Users Role.
     * @param caller the User calling this method.
     * @return the new UserOrganization object saved.
     * @throws NoSuchElementException if Organization is not found, or the User is not an employee.
     * @throws UsernameNotFoundException if User is not found.
     * @throws IllegalArgumentException if the caller is not an admin in the Organization.
     */
    public UserOrganization editEmployee(EmployeeRequest request, User caller)  throws NoSuchElementException, UsernameNotFoundException, IllegalArgumentException{
        UserOrganization admin = getAdmin(request.getOrgNum(), caller);
        Organization org = admin.getOrganization();
        User user = (User) userService.loadUserByUsername(request.getEmail());

        //Fetch old UserOrg and change role
        Optional<UserOrganization> userOrganizationOpt = userOrgRepository.getByUserAndOrganization(user, org);
        if(userOrganizationOpt.isEmpty()) throw new NoSuchElementException("This User is not an employee");
        UserOrganization oldUserOrg = userOrganizationOpt.get();
        oldUserOrg.setOrganizationRole(request.getRole());

        return userOrgRepository.save(oldUserOrg);
    }

    /**
     * Tries to get the UserOrganization relation between the caller and the Organization in the Request.
     * Throws error if the objects given are not found or if the caller is not Admin.
     * @see Organization
     * @see User
     * @param orgNum the orgNum from the request.
     * @param caller the User calling this method.
     * @return if found, the UserOrganization object between the caller and the Organization in the Request object.
     * @throws NoSuchElementException if Organization or User is not found.
     * @throws IllegalArgumentException if the caller is not an Admin in the Organization.
     */
    private UserOrganization getAdmin(Long orgNum, User caller) throws NoSuchElementException, IllegalArgumentException{
        //Fetch organization, throws if not found
        Organization org = organizationService.getOrganization(orgNum);

        //Fetch UserOrg between caller and Org, check if admin
        Optional<UserOrganization> userOrgOpt = userOrgRepository.getByUserAndOrganization(caller, org);
        if(userOrgOpt.isEmpty() || !userOrgOpt.get().getOrganizationRole().equals(OrganizationRole.ADMIN)) throw new IllegalArgumentException("Caller is not an admin in the Organization");

        return userOrgOpt.get();
    }

    /**
     * Add an existing user as an employee for an Organization with a given role.
     * @param request the request.
     * @param caller the User calling this method.
     * @return the created UserOrganization object.
     * @throws NoSuchElementException if Organization is not found.
     * @throws UsernameNotFoundException if User is not found.
     * @throws IllegalArgumentException if the caller is not an admin in the Organization,
     * or the User is already an employee.
     */
    public UserOrganization addEmployee(EmployeeRequest request, User caller) throws NoSuchElementException, UsernameNotFoundException, IllegalArgumentException{
        UserOrganization admin = getAdmin(request.getOrgNum(), caller);
        User user = (User) userService.loadUserByUsername(request.getEmail());
        Optional<UserOrganization> userOrgOpt = userOrgRepository.getByUserAndOrganization(user, admin.getOrganization());
        if(userOrgOpt.isPresent()) throw new IllegalArgumentException("User is already an employee");

        //Add new employee
        UserOrganization userOrganization = new UserOrganization(user, admin.getOrganization(), request.getRole());
        return userOrgRepository.save(userOrganization);
    }

    /**
     * Delete an employee from an organization. If this is the last employee, the organization will also be deleted.
     * @see Organization
     * @param caller the User calling this method.
     * @throws NoSuchElementException if Organization or employee is not found.
     * @throws UsernameNotFoundException if User is not found.
     * @throws IllegalArgumentException if the caller is not an admin in the Organization.
     */
    public void deleteEmployee(Long orgNum, Long userId, User caller) throws NoSuchElementException, UsernameNotFoundException, IllegalArgumentException{
        //Verify caller and requested User
        UserOrganization admin = getAdmin(orgNum, caller);
        User user = (User) userService.getUserById(userId);
        if(user == null) throw new UsernameNotFoundException("User does not exist");

        //Verify Organization
        Organization organization = admin.getOrganization();

        //Verify that User is an employee
        Optional<UserOrganization> employeeOpt = userOrgRepository.getByUserAndOrganization(user, organization);
        if(employeeOpt.isEmpty()) throw new NoSuchElementException("The User is not an employee in the Organization");

        //Check if this is the last employee
        if(organization.getUserOrganizations().size() == 1){
            organizationService.deleteOrganization(organization.getOrgNum(), caller);
        }else{
            userOrgRepository.delete(employeeOpt.get());
        }
    }
}
