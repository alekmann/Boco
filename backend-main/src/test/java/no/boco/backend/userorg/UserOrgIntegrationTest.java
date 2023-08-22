package no.boco.backend.userorg;

import no.boco.backend.organization.Organization;
import no.boco.backend.organization.OrganizationRepository;
import no.boco.backend.user.Role;
import no.boco.backend.user.User;
import no.boco.backend.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserOrgIntegrationTest {
    @Autowired
    UserOrganizationRepository userOrgRepository;
    @Autowired
    UserOrganizationService userOrgService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    OrganizationRepository orgRepository;

    @BeforeEach
    public void fillTestData() {
        User user = new User("ola@normann.no", "abc", "Ola", "Normann", null,
                Date.valueOf("2001-01-01"), Role.USER);
        user = userRepository.save(user);
        Organization organization = new Organization(40L, "Coop Bygg");
        organization = orgRepository.save(organization);

        UserOrganization userOrg = new UserOrganization(user, organization, OrganizationRole.EMPLOYEE);
        userOrgRepository.save(userOrg);
    }

    @AfterEach
    public void emptyDatabase() {
        userOrgRepository.deleteAll();
        userRepository.deleteAll();
        orgRepository.deleteAll();
    }

    @Nested
    class createOrganization{
        @Test
        void createOrganizationTest() {
            User creator = userRepository.findByEmail("ola@normann.no").get();
            Organization org = new Organization(953544741L, "Number_1");

            try{
                Organization org1 = userOrgService.createOrganization(org, creator);
                Assertions.assertEquals(953544741L, (long) org1.getOrgNum());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                fail();
            }

        }

        @Test
        void organizationListRightSizeTest() {
            orgRepository.deleteAll();
            User creator = userRepository.findByEmail("ola@normann.no").get();
            Organization org1 = new Organization(953544741L, "Number_1");
            Organization org2 = new Organization(980393313L, "Number_2");

            try {
                userOrgService.createOrganization(org1, creator);
                userOrgService.createOrganization(org2, creator);
                Assertions.assertEquals(2, orgRepository.findAll().size());

            } catch (Exception e) {
                System.out.println(e.getMessage());
                fail();
            }
        }

        @Test
        void createDuplicateOrganizationFail() {
            User creator = userRepository.findByEmail("ola@normann.no").get();
            Organization org1 = new Organization(980393313L, "Number_1");
            try {
                userOrgService.createOrganization(org1, creator);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                fail();
            }

            try {
                userOrgService.createOrganization(org1, creator);
            } catch (Exception e) {
            }
        }

        @Test
        void getOrgNameByOrgNumAPITest(){
            User creator = userRepository.findByEmail("ola@normann.no").get();
            Organization org1 = new Organization(980393313L, "Number_1");
            try {
                userOrgService.createOrganization(org1, creator);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                fail();
            }

            Assertions.assertEquals("COOP MEGA SA", orgRepository.findById(980393313L).get().getOrgName());
        }
    }

    @Nested
    class getByUserAndOrg{
        @Test
        public void finds_valid_pair(){
            User user = userRepository.findByEmail("ola@normann.no").get();
            Organization org = orgRepository.findById(40L).get();

            Optional<UserOrganization> userOrg = userOrgRepository.getByUserAndOrganization(user, org);
            assertTrue(userOrg.isPresent());
        }

        @Test
        public void returns_empty_on_non_valid_pair(){
            //Save new objects but no relations between them
            User user = new User("jesper@normann.no", "abc", "Jesper", "Normann", null,
                    Date.valueOf("2001-01-01"), Role.USER);
            user = userRepository.save(user);
            Organization organization = new Organization(50L, "Ikea Leangen");
            organization = orgRepository.save(organization);

            Optional<UserOrganization> userOrg = userOrgRepository.getByUserAndOrganization(user, organization);
            assertTrue(userOrg.isEmpty());
        }
    }

    private void fillEmployees(){
        userOrgRepository.deleteAll();
        User user1 = new User("admin@coop.no","abc","Admin","Adminson",null,Date.valueOf("2000-01-01"), Role.USER);
        user1 = userRepository.save(user1);
        User user2 = new User("employee@coop.no","abc","Emp","Loyee",null,Date.valueOf("2005-01-01"),Role.USER);
        user2 = userRepository.save(user2);

        Organization organization = new Organization(40L,"Coop");
        organization = orgRepository.save(organization);

        UserOrganization userOrganization = new UserOrganization(user1,organization, OrganizationRole.ADMIN);
        userOrganization = userOrgRepository.save(userOrganization);
    }

    @Nested
    class addEmployee{
        @Test
        public void method_adds_employee(){
            fillEmployees();
            User user1 = userRepository.findByEmail("admin@coop.no").get();
            Organization organization = orgRepository.findById(40L).get();

            //Add employee in organization
            EmployeeRequest request = new EmployeeRequest("employee@coop.no",40L,OrganizationRole.EMPLOYEE);
            try {
                userOrgService.addEmployee(request, user1);
                List<UserOrganization> userOrgs = userOrgRepository.findAllByOrganization(organization);
                for(UserOrganization userOrg : userOrgs){
                    System.out.println(userOrg.toString());
                }
                assertEquals(2, userOrgs.size());
            }catch(NoSuchElementException | IllegalArgumentException | UsernameNotFoundException e){
                fail();
            }
        }

        @Test
        public void handles_wrong_user(){
            fillEmployees();
            User user1 = userRepository.findByEmail("admin@coop.no").get();

            EmployeeRequest request = new EmployeeRequest("wrong@wrong.no",40L,OrganizationRole.EMPLOYEE);
            try{
                userOrgService.addEmployee(request, user1);
                fail();
            }catch(UsernameNotFoundException e){
                //pass test
            }catch(NoSuchElementException | IllegalArgumentException e){
                fail();
            }
        }

        @Test
        public void handles_wrong_org(){
            fillEmployees();
            User user1 = userRepository.findByEmail("admin@coop.no").get();

            EmployeeRequest request = new EmployeeRequest("wrong@wrong.no",56L,OrganizationRole.EMPLOYEE);
            try{
                userOrgService.addEmployee(request, user1);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }catch(UsernameNotFoundException | IllegalArgumentException e){
                fail();
            }
        }

        @Test
        public void rejects_non_admin_calling(){
            fillEmployees();
            User user2 = userRepository.findByEmail("employee@coop.no").get();

            //Add employee in organization from the employee
            EmployeeRequest request = new EmployeeRequest("employee@coop.no",40L,OrganizationRole.EMPLOYEE);
            try {
                userOrgService.addEmployee(request, user2);
                fail();
            }catch(IllegalArgumentException e){
                //pass test
            }catch(UsernameNotFoundException | NoSuchElementException e){
                fail();
            }
        }
    }

    @Nested
    class editEmployee{
        @Test
        public void method_changes_userOrg(){
            fillEmployees();
            User user1 = userRepository.findByEmail("admin@coop.no").get();
            User user2 = userRepository.findByEmail("employee@coop.no").get();
            Organization organization = orgRepository.findById(40L).get();
            UserOrganization userOrganization = new UserOrganization(user2,organization, OrganizationRole.EMPLOYEE);
            userOrganization = userOrgRepository.save(userOrganization);

            assertEquals(OrganizationRole.EMPLOYEE,userOrganization.getOrganizationRole());

            //Change user2 to admin
            EmployeeRequest request = new EmployeeRequest(user2.getEmail(), organization.getOrgNum(), OrganizationRole.ADMIN);
            UserOrganization response = userOrgService.editEmployee(request, user1);

            assertEquals(OrganizationRole.ADMIN,response.getOrganizationRole());
        }

        @Test
        public void handles_wrong_user(){
            fillEmployees();
            User user1 = userRepository.findByEmail("admin@coop.no").get();

            EmployeeRequest request = new EmployeeRequest("wrong@wrong.no",40L,OrganizationRole.EMPLOYEE);
            try{
                userOrgService.editEmployee(request, user1);
                fail();
            }catch(UsernameNotFoundException e){
                //pass test
            }catch(NoSuchElementException | IllegalArgumentException e){
                fail();
            }
        }

        @Test
        public void handles_wrong_org(){
            fillEmployees();
            User user1 = userRepository.findByEmail("admin@coop.no").get();

            EmployeeRequest request = new EmployeeRequest("wrong@wrong.no",56L,OrganizationRole.EMPLOYEE);
            try{
                userOrgService.editEmployee(request, user1);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }catch(UsernameNotFoundException | IllegalArgumentException e){
                fail();
            }
        }

        @Test
        public void rejects_non_admin_calling(){
            fillEmployees();
            User user2 = userRepository.findByEmail("employee@coop.no").get();
            Organization organization = orgRepository.findById(40L).get();
            UserOrganization userOrganization = new UserOrganization(user2,organization, OrganizationRole.EMPLOYEE);
            userOrganization = userOrgRepository.save(userOrganization);

            //Employee tries to change itself to admin
            EmployeeRequest request = new EmployeeRequest("employee@coop.no",40L,OrganizationRole.ADMIN);
            try {
                userOrgService.editEmployee(request, user2);
                fail();
            }catch(IllegalArgumentException e){
                //pass test
            }catch(UsernameNotFoundException | NoSuchElementException e){
                fail();
            }
        }

        @Test
        public void handles_non_employee(){
            fillEmployees();
            User user1 = userRepository.findByEmail("admin@coop.no").get();

            //Try to change user2 to admin
            EmployeeRequest request = new EmployeeRequest("employee@coop.no", 40L, OrganizationRole.ADMIN);
            try {
                userOrgService.editEmployee(request, user1);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }catch(UsernameNotFoundException | IllegalArgumentException e){
                fail();
            }
        }
    }

    @Nested
    class searchEmployees{
        @Test
        public void method_returns_matches(){
            fillEmployees();
            User user2 = userRepository.findByEmail("employee@coop.no").get();
            Organization organization = orgRepository.findById(40L).get();
            UserOrganization userOrganization = new UserOrganization(user2,organization, OrganizationRole.EMPLOYEE);
            userOrganization = userOrgRepository.save(userOrganization);

            //Search by one letter
            List<UserOrganization> result1 = userOrgService.searchEmployees(40L, "A", user2);
            //Search by first name
            List<UserOrganization> result2 = userOrgService.searchEmployees(40L, "Admin", user2);
            //Search by full name
            List<UserOrganization> result3 = userOrgService.searchEmployees(40L, "Admin Adminson", user2);
            //Search by last three letters of user 2
            List<UserOrganization> result4 = userOrgService.searchEmployees(40L, "yee", user2);
            //Search with no match
            List<UserOrganization> result5 = userOrgService.searchEmployees(40L, "John Travolta", user2);

            assertTrue(result1.size() == 1 && result1.get(0).getUser().getFirstName().equals("Admin"));
            assertTrue(result2.size() == 1 && result2.get(0).getUser().getFirstName().equals("Admin"));
            assertTrue(result3.size() == 1 && result3.get(0).getUser().getFirstName().equals("Admin"));
            assertTrue(result4.size() == 1 && result4.get(0).getUser().getFirstName().equals("Emp"));
            assertTrue(result5.isEmpty());
        }

        @Test
        public void handles_wrong_org(){
            fillEmployees();
            User user2 = userRepository.findByEmail("employee@coop.no").get();
            Organization organization = orgRepository.findById(40L).get();
            UserOrganization userOrganization = new UserOrganization(user2,organization, OrganizationRole.EMPLOYEE);
            userOrganization = userOrgRepository.save(userOrganization);

            try{
                List<UserOrganization> result = userOrgService.searchEmployees(57L, "Admin", user2);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }

        @Test
        public void handles_non_employee(){
            fillEmployees();
            User user2 = userRepository.findByEmail("employee@coop.no").get();

            try{
                List<UserOrganization> result = userOrgService.searchEmployees(40L, "Admin", user2);
                fail();
            }catch(IllegalArgumentException e){
                //pass test
            }
        }
    }

    @Nested
    class deleteEmployee{
        @Test
        public void deletes_userOrg(){
            fillEmployees();
            User user2 = userRepository.findByEmail("employee@coop.no").get();
            Organization organization = orgRepository.findById(40L).get();
            UserOrganization userOrganization = new UserOrganization(user2,organization, OrganizationRole.EMPLOYEE);
            userOrgRepository.save(userOrganization);

            //Delete user2
            userOrgService.deleteEmployee(40L, user2.getId(), userRepository.findByEmail("admin@coop.no").get());

            assertEquals(1, userOrgRepository.findAll().size());
            assertEquals("admin@coop.no", userOrgRepository.findAll().get(0).getUser().getEmail());
            assertFalse(orgRepository.findAll().isEmpty());
        }

        @Test
        public void deletes_organization_when_last_employee(){
            fillEmployees();
            User user1 = userRepository.findByEmail("admin@coop.no").get();

            assertFalse(userOrgRepository.findAll().isEmpty());
            assertFalse(orgRepository.findAll().isEmpty());

            //Delete user1
            userOrgService.deleteEmployee(40L, user1.getId(), userRepository.findByEmail("admin@coop.no").get());

            assertTrue(userOrgRepository.findAll().isEmpty());
            assertTrue(orgRepository.findAll().isEmpty());
        }
    }
}
