package no.boco.backend.organization;

import no.boco.backend.user.Role;
import no.boco.backend.user.User;
import no.boco.backend.user.UserRepository;
import no.boco.backend.userorg.OrganizationRole;
import no.boco.backend.userorg.UserOrganization;
import no.boco.backend.userorg.UserOrganizationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OrganizationIntegrationTest {
    private static final String USER_EMAIL = "test@test.no";

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserOrganizationRepository userOrganizationRepository;

    @BeforeEach
    public void createUser() {
        User user = new User(USER_EMAIL,"test","Test","Testesen",null,Date.valueOf("2000-01-01"), Role.USER);
        userRepository.save(user);
    }

    @AfterEach
    public void emptyDatabase() {
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        userOrganizationRepository.deleteAll();
    }

    private void fillEmployees(){
        User user1 = new User("admin@coop.no","abc","Admin","Adminson",null,Date.valueOf("2000-01-01"), Role.USER);
        user1 = userRepository.save(user1);
        User user2 = new User("employee@coop.no","abc","Emp","Loyee",null,Date.valueOf("2005-01-01"),Role.USER);
        user2 = userRepository.save(user2);

        Organization organization = new Organization(40L,"Coop");
        organization = organizationRepository.save(organization);

        UserOrganization userOrganization = new UserOrganization(user1,organization, OrganizationRole.ADMIN);
        userOrganization = userOrganizationRepository.save(userOrganization);
    }

    @Test
    public void deleteOrganizationTest(){
        fillEmployees();
        User user1 = userRepository.findByEmail("admin@coop.no").get();
        try{
            organizationService.deleteOrganization(40L, user1);
            assertEquals(0, organizationRepository.findAll().size());
        } catch (Exception e){
            fail();
        }
    }

    @Nested
    class getOrganization{
        @Test
        public void methods_finds_org(){
            Organization organization = new Organization(980393313L,"Coop");
            organization = organizationRepository.save(organization);

            try {
                Organization result = organizationService.getOrganization(organization.getOrgNum());
                assertEquals(organization.getOrgName(),result.getOrgName());
            }catch(NoSuchElementException e){
                fail();
            }
        }

        @Test
        public void throws_at_wrong_org(){
            assertTrue(organizationRepository.findAll().isEmpty());
            try {
                Organization result = organizationService.getOrganization(40L);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }
    }

    @Nested
    class editOrganization{
        @Test
        public void method_changes_org(){
            //Save objects
            User user = new User("ola@normann.no","abc","Ola","Normann",null,Date.valueOf("2000-01-01"), Role.USER);
            user = userRepository.save(user);
            Organization organization = new Organization(40L,"Coop");
            organization = organizationRepository.save(organization);
            UserOrganization userOrganization = new UserOrganization(user, organization, OrganizationRole.ADMIN);
            userOrganization = userOrganizationRepository.save(userOrganization);

            //Edit organization
            organization.setNickname("Coop Bygg");

            organizationService.editOrganization(organization, user);
            assertEquals("Coop Bygg", organizationRepository.findById(40L).get().getNickname());
        }

        @Test
        public void handles_wrong_org(){
            //Save objects
            User user = new User("ola@normann.no","abc","Ola","Normann",null,Date.valueOf("2000-01-01"), Role.USER);
            user = userRepository.save(user);
            Organization organization = new Organization(40L,"Coop");
            organization = organizationRepository.save(organization);
            UserOrganization userOrganization = new UserOrganization(user, organization, OrganizationRole.ADMIN);
            userOrganization = userOrganizationRepository.save(userOrganization);

            //Edit organization
            organization.setNickname("Coop Bygg");
            organization.setOrgNum(56L);

            try {
                organizationService.editOrganization(organization, user);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }

        @Test
        public void handles_non_employee(){
            //Save objects
            User user = new User("ola@normann.no","abc","Ola","Normann",null,Date.valueOf("2000-01-01"), Role.USER);
            user = userRepository.save(user);
            Organization organization = new Organization(40L,"Coop");
            organization = organizationRepository.save(organization);

            //Edit organization
            organization.setNickname("Coop Bygg");
            try {
                organizationService.editOrganization(organization, user);
                fail();
            }catch(IllegalArgumentException e){
                //pass test
            }
        }

        @Test
        public void handles_non_admin(){
            //Save objects
            User user = new User("ola@normann.no","abc","Ola","Normann",null,Date.valueOf("2000-01-01"), Role.USER);
            user = userRepository.save(user);
            Organization organization = new Organization(40L,"Coop");
            organization = organizationRepository.save(organization);
            UserOrganization userOrganization = new UserOrganization(user, organization, OrganizationRole.EMPLOYEE);
            userOrganization = userOrganizationRepository.save(userOrganization);

            //Edit organization
            organization.setNickname("Coop Bygg");
            try {
                organizationService.editOrganization(organization, user);
                fail();
            }catch(IllegalArgumentException e){
                //pass test
            }
        }

        @Test
        public void change_organization_profile_picture() {
            //Save objects
            Organization organization = new Organization(40L,"Coop");
            organization = organizationRepository.save(organization);

            String picture = "\"me\"";
            organizationService.changePicture(organization, picture);

            assertTrue(organization.getProfilePicture().equals("me"));
        }
    }

    @Nested
    class getEmployees{
        @Test
        public void method_gets_employees(){
            fillEmployees();
            User user2 = userRepository.findByEmail("employee@coop.no").get();
            Organization organization = organizationRepository.findById(40L).get();
            UserOrganization userOrganization = new UserOrganization(user2,organization, OrganizationRole.EMPLOYEE);
            userOrganization = userOrganizationRepository.save(userOrganization);

            try {
                Set<UserOrganization> userOrgs = organizationService.getEmployees(organization.getOrgNum(), user2);
                assertEquals(2, userOrgs.toArray().length);
            }catch(IllegalArgumentException | NoSuchElementException e){
                fail();
            }
        }

        @Test
        public void handles_wrong_org(){
            fillEmployees();
            User user1 = userRepository.findByEmail("admin@coop.no").get();

            try {
                Set<UserOrganization> userOrgs = organizationService.getEmployees(57L, user1);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }catch(IllegalArgumentException e){
                fail();
            }
        }

        @Test
        public void handles_non_employee(){
            fillEmployees();
            User user2 = userRepository.findByEmail("employee@coop.no").get();

            try {
                Set<UserOrganization> userOrgs = organizationService.getEmployees(40L, user2);
                fail();
            }catch(NoSuchElementException e){
                fail();
            }catch(IllegalArgumentException e){
                //pass test
            }
        }
    }
}
