package no.boco.backend.post;

import no.boco.backend.category.Category;
import no.boco.backend.category.CategoryRepository;
import no.boco.backend.location.Location;
import no.boco.backend.location.LocationRepository;
import no.boco.backend.organization.Organization;
import no.boco.backend.organization.OrganizationRepository;
import no.boco.backend.picture.Picture;
import no.boco.backend.picture.PictureRepository;
import no.boco.backend.rental.Rental;
import no.boco.backend.rental.RentalRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PostIntegrationTest {

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    OrganizationRepository orgRepository;
    @Autowired
    UserOrganizationRepository userOrgRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PictureRepository pictureRepository;
    @Autowired
    RentalRepository rentalRepository;
    @Autowired
    LocationRepository locationRepository;

    @BeforeEach
    public void fillTestData() {
        User user = new User("ola@normann.no", "abc", "Ola", "Normann", null,
                Date.valueOf("2001-01-01"), null);
        userRepository.save(user);

        Organization org = new Organization(40L, "Coop Bygg");
        orgRepository.save(org);

        Category tools = new Category("Tools");
        categoryRepository.save(tools);

        Category electronics = new Category("Electronics");
        categoryRepository.save(electronics);
    }

    @AfterEach
    public void emptyDatabase() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        orgRepository.deleteAll();
        userOrgRepository.deleteAll();
        categoryRepository.deleteAll();
        pictureRepository.deleteAll();
        rentalRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @Nested
    class add{
        @Test
        public void handles_correct_foreign_keys(){
            //Verify that table is empty
            assertEquals(0, postRepository.findAll().size());

            //Save new post
            User user = userRepository.findAll().get(0);
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, user, null, new HashSet<>());
            try {
                postService.add(post, user);
            }catch(IllegalAccessException | NoSuchElementException e){
                fail();
            }

            //Verify that the post is saved
            assertTrue(postRepository.findAll().size() > 0);
            assertEquals(postRepository.findAll().get(0).getTitle(), "Hammer");
        }

        @Test
        public void handles_wrong_foreign_keys(){
            //Create new objects but don't save them
            User user = new User("johan@normann.no", "abc", "Johan", "Normann", null,
                    Date.valueOf("2001-01-01"), null);
            Category category = new Category("Kategori");

            //Try to save post
            Post post = new Post("Hammer", 40, category, "", null, user, null, new HashSet<>());
            try {
                postService.add(post, user);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }catch(IllegalAccessException e){
                fail();
            }
        }

//        @Test
//        public void user_in_org_can_add_post_to_org(){
//            //Add user to organization
//            User user = userRepository.findAll().get(0);
//            Organization organization = orgRepository.findAll().get(0);
//            UserOrganization userOrg = new UserOrganization(user, organization, OrganizationRole.EMPLOYEE);
//            userOrgRepository.save(userOrg);
//
//            //Add post in organization from user
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, user, organization, new HashSet<>());
//            try{
//                postService.add(post, user);
//            }catch(IllegalAccessException | NoSuchElementException e){
//                fail();
//            }
//        }

        @Test
        public void user_out_of_org_cant_add_post_to_org(){
            User user = userRepository.findAll().get(0);
            Organization organization = orgRepository.findAll().get(0);

            //Try to add post in organization from user
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, user, organization, new HashSet<>());
            try{
                postService.add(post, user);
                fail();
            }catch(IllegalAccessException e){
                //pass test
            }catch(NoSuchElementException e){
                fail();
            }
        }
    }

    @Nested
    class edit{
        @Test
        public void method_edits_post(){
            //Save a post
            User user = userRepository.findAll().get(0);
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, user, null, new HashSet<>());
            post = postRepository.save(post);
            assertTrue(postRepository.existsById(post.getPostId()));

            //Edit post
            post.setTitle("Sag");
            try {
                postService.edit(post, user);
                post = postRepository.findById(post.getPostId()).get();
                assertEquals("Sag", post.getTitle());
            }catch(NoSuchElementException | IllegalAccessException e){
                fail();
            }
        }

        @Test
        public void handles_non_existing_post(){
            User user = userRepository.findAll().get(0);
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, user, null, new HashSet<>());
            try{
                postService.edit(post, user);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }catch(IllegalAccessException e){
                fail();
            }
        }

        @Test
        public void handles_different_user(){
            //Save post from user and save otherUser
            User user = userRepository.findAll().get(0);
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, user, null, new HashSet<>());
            postRepository.save(post);
            User otherUser = new User("wrong@user.com", "abc", "Johan", "Normann", null,
                    Date.valueOf("2001-01-01"), null);
            userRepository.save(otherUser);

            //Try to edit post from otherUser
            try{
                post.setTitle("Sag");
                postService.edit(post, otherUser);
                fail();
            }catch(NoSuchElementException e){
                fail();
            }catch(IllegalAccessException e){
                //pass test
            }
        }

        @Test
        public void allows_admin_of_org_to_edit(){
            //Add user to organization
            User user = userRepository.findAll().get(0);
            Organization organization = orgRepository.findAll().get(0);
            UserOrganization userOrg = new UserOrganization(user, organization, OrganizationRole.EMPLOYEE);
            userOrgRepository.save(userOrg);

            //Add post in organization from user
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, user, organization, new HashSet<>());
            postRepository.save(post);

            //Edit post
            try{
                post.setTitle("Sag");
                postService.edit(post, user);
            }catch(IllegalAccessException | NoSuchElementException e){
                fail();
            }
        }

        @Test
        public void handles_non_existing_category(){
            //Save new post
            User user = userRepository.findAll().get(0);
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, user, null, new HashSet<>());
            postRepository.save(post);

            //Create wrong category
            Category category = new Category("Wrong");

            //Try to edit post
            try{
                post.setCategory(category);
                postService.edit(post, user);
                fail();
            }catch(IllegalAccessException e){
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }
    }

    @Nested
    class delete{
        @Test
        public void handles_existing_post(){
            assertEquals(0, postRepository.findAll().size());
            User user = userRepository.findAll().get(0);

            //Save new post
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, userRepository.findAll().get(0), null, new HashSet<>());
            Long postId = postRepository.save(post).getPostId();

            assertEquals(1, postRepository.findAll().size());

            //Delete the new post
            try{
                postService.delete(postId, user);
            }catch(IllegalAccessException e){
                fail();
            }

            assertEquals(0, postRepository.findAll().size());
        }

        @Test
        public void handles_wrong_post(){
            assertEquals(0, postRepository.findAll().size());
            User user = userRepository.findAll().get(0);

            try {
                postService.delete(1L, user);
                fail();
            }catch(IllegalAccessException e){
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }

        @Test
        public void handles_wrong_user(){
            //Create new user
            User wrongUser = new User("johan@normann.no", "abc", "Johan", "Normann", null,
                    Date.valueOf("2001-01-01"), null);
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, userRepository.findAll().get(0), null, new HashSet<>());
            Long postId = postRepository.save(post).getPostId();

            try {
                postService.delete(postId, wrongUser);
                fail();
            }catch(IllegalAccessException e){
                //pass test
            }
        }

        @Test
        public void admin_can_delete_org_post(){
            //Create objects
            Organization org = orgRepository.findAll().get(0);
            User employee = userRepository.findAll().get(0);

            //Create new post from employee in the organization
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, employee, org, new HashSet<>());
            postRepository.save(post);

            //Make an admin of the organization
            User admin = new User("johan@normann.no", "abc", "Johan", "Normann", null,
                    Date.valueOf("2001-01-01"), null);
            admin = userRepository.save(admin);
            UserOrganization userOrg = new UserOrganization(admin, org, OrganizationRole.ADMIN);
            userOrgRepository.save(userOrg);

            //Make the admin try to delete the employees post
            try {
                postService.delete(post.getPostId(), admin);
            }catch(IllegalAccessException e){
                fail();
            }
        }

        @Test
        public void deletes_dependandt_objects(){
            assertTrue(postRepository.findAll().isEmpty());

            //Create new post
            User owner = userRepository.findAll().get(0);
            User customer = new User("johan@normann.no", "abc", "Johan", "Normann", null,
                    Date.valueOf("2001-01-01"), Role.USER);
            userRepository.save(customer);
            Category category = categoryRepository.findAll().get(0);
            Post post = new Post("Hammer", 40, category, "", null, owner, null, new HashSet<>());
            post = postRepository.save(post);

            assertFalse(postRepository.findAll().isEmpty());

            //Add picture and rental to post
            Picture picture = new Picture("test.png", "filedata");
            picture.setPost(post);
            picture = pictureRepository.save(picture);

            Rental rental = new Rental(Date.valueOf("2022-10-01"),Date.valueOf("2022-10-20"),post,customer,false);
            rental = rentalRepository.save(rental);

            //Verify that post and pictures got added to post
            post = postRepository.findById(post.getPostId()).get();
            assertFalse(post.getRentals().isEmpty());
            assertFalse(post.getPictures().isEmpty());

            //Delete post
            try {
                postService.delete(post.getPostId(), owner);
                assertTrue(postRepository.findAll().isEmpty());
            }catch(IllegalAccessException | NoSuchElementException e){
                fail();
            }

            //Check that picture and rental also got deleted
            assertTrue(pictureRepository.findAll().isEmpty());
            assertTrue(rentalRepository.findAll().isEmpty());
        }
    }

    @Test
    public void handles_invalid_page_args(){
        //This method is called by multiple methods, testing one will test all cases.
        try{
            Page<Post> posts = postService.getByUser(-5, 5, 1L);
            fail();
        }catch(IllegalArgumentException e){
            //pass test
        }
    }

    @Nested
    class getByUser{
        @Test
        public void handles_existing_user(){
            //Save post
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, userRepository.findAll().get(0), null, new HashSet<>());
            postRepository.save(post);

            //Get posts for user
            Long userId = userRepository.findAll().get(0).getId();
            Page<Post> posts = postService.getByUser(0, 5, userId);

            assertEquals(1, posts.getTotalElements());
            assertEquals("Hammer", posts.get().toList().get(0).getTitle());
        }

        @Test
        public void handles_invalid_user(){
            Long correctUserId = userRepository.findAll().get(0).getId();
            Long wrongUserId = correctUserId + 1;

            try {
                Page<Post> posts = postService.getByUser(0, 5, wrongUserId);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }

        @Test
        public void correct_pagination(){
            //Create new objects
            User user = new User("johan@normann.no", "abc", "Johan", "Normann", null,
                    Date.valueOf("2001-01-01"), null);
            userRepository.save(user);
            User user1 = userRepository.findAll().get(0);
            User user2 = userRepository.findAll().get(1);
            Category category1 = categoryRepository.findAll().get(0);
            Category category2 = categoryRepository.findAll().get(1);

            //Save 10 new posts
            for(int i=0; i<6; i++){
                Post post = new Post("Hammer " + (i+1), 40, category1, "", null, user1, null, new HashSet<>());
                postRepository.save(post);
            }
            for(int i=0; i<6; i++){
                Post post = new Post("Sag " + (i+1), 50, category2, "", null, user2, null, new HashSet<>());
                postRepository.save(post);
            }

            //Ensure correct result size and correct objects found for user 1
            Page<Post> posts1 = postService.getByUser(0, 6, user1.getId());
            assertEquals(6, posts1.toList().size());
            for(Post aPost : posts1){
                assertTrue(aPost.getTitle().contains("Hammer"));
                assertFalse(aPost.getTitle().contains("Sag"));
            }

            //Ensure correct result size and correct objects found for user 2
            Page<Post> posts2 = postService.getByUser(0, 6, user2.getId());
            assertEquals(6, posts2.toList().size());
            for(Post aPost : posts2){
                assertTrue(aPost.getTitle().contains("Sag"));
                assertFalse(aPost.getTitle().contains("Hammer"));
            }

            //Ensure correct page size when splitting
            posts1 = postService.getByUser(0, 3, user1.getId());
            posts2 = postService.getByUser(1, 3, user1.getId());
            assertEquals(3, posts1.toList().size());
            assertEquals(3, posts2.toList().size());

            //Ensure no duplicates in the two pages
            for(Post post1 : posts1){
                for(Post post2 : posts2){
                    assertNotEquals(post1.getPostId(), post2.getPostId());
                }
            }
        }
    }

    @Nested
    class getByOrganization{
        @Test
        public void handles_existing_organization(){
            //Save post
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", null, userRepository.findAll().get(0), orgRepository.findAll().get(0), new HashSet<>());
            postRepository.save(post);

            //Get posts for organization
            Long orgNum = orgRepository.findAll().get(0).getOrgNum();
            Page<Post> posts = postService.getByOrganization(0, 5, orgNum);

            assertEquals(1, posts.getTotalElements());
            assertEquals("Hammer", posts.get().toList().get(0).getTitle());
        }

        @Test
        public void handles_invalid_organization(){
            Long correctOrgNum = orgRepository.findAll().get(0).getOrgNum();
            Long wrongOrgNum = correctOrgNum + 1;

            try {
                Page<Post> posts = postService.getByOrganization(0, 5, wrongOrgNum);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }

        @Test
        public void correct_pagination(){
            //Create new objects
            Organization org = new Organization(50L, "IKEA Leangen");
            orgRepository.save(org);
            Organization org1 = orgRepository.findAll().get(0);
            Organization org2 = orgRepository.findAll().get(1);
            User user = userRepository.findAll().get(0);
            Category category1 = categoryRepository.findAll().get(0);
            Category category2 = categoryRepository.findAll().get(1);

            //Save 10 new posts
            for(int i=0; i<6; i++){
                Post post = new Post("Hammer " + (i+1), 40, category1, "", null, user, org1, new HashSet<>());
                postRepository.save(post);
            }
            for(int i=0; i<6; i++){
                Post post = new Post("Sag " + (i+1), 50, category2, "", null, user, org2, new HashSet<>());
                postRepository.save(post);
            }

            //Ensure correct result size and correct objects found for organization 1
            Page<Post> posts1 = postService.getByOrganization(0, 6, org1.getOrgNum());
            assertEquals(6, posts1.toList().size());
            for(Post aPost : posts1){
                assertTrue(aPost.getTitle().contains("Hammer"));
                assertFalse(aPost.getTitle().contains("Sag"));
            }

            //Ensure correct result size and correct objects found for organization 2
            Page<Post> posts2 = postService.getByOrganization(0, 6, org2.getOrgNum());
            assertEquals(6, posts2.toList().size());
            for(Post aPost : posts2){
                assertTrue(aPost.getTitle().contains("Sag"));
                assertFalse(aPost.getTitle().contains("Hammer"));
            }

            //Ensure correct page size when splitting
            posts1 = postService.getByOrganization(0, 3, org1.getOrgNum());
            posts2 = postService.getByOrganization(1, 3, org2.getOrgNum());
            assertEquals(3, posts1.toList().size());
            assertEquals(3, posts2.toList().size());

            //Ensure no duplicates in the two pages
            for(Post post1 : posts1){
                for(Post post2 : posts2){
                    assertNotEquals(post1.getPostId(), post2.getPostId());
                }
            }
        }
    }

    @Nested
    class getPosts{
        @Test
        public void correct_pagination(){
            //Create new objects
            User user = new User("johan@normann.no", "abc", "Johan", "Normann", null,
                    Date.valueOf("2001-01-01"), null);
            userRepository.save(user);
            User user1 = userRepository.findAll().get(0);
            User user2 = userRepository.findAll().get(1);
            Category category1 = categoryRepository.findAll().get(0);
            Category category2 = categoryRepository.findAll().get(1);

            //Save 10 new posts
            for(int i=0; i<10; i++){
                Post post = new Post("Hammer " + (i+1), 40, category1, "", null, user1, null, new HashSet<>());
                postRepository.save(post);
            }

            //Ensure correct result size and correct objects found
            Page<Post> posts = postService.getPosts(0, 10);
            assertEquals(10, posts.toList().size());
            for(Post aPost : posts){
                assertTrue(aPost.getTitle().contains("Hammer"));
            }

            //Ensure correct page size when splitting
            Page<Post> posts1 = postService.getPosts(0, 5);
            Page<Post> posts2 = postService.getPosts(1, 5);
            assertEquals(5, posts1.toList().size());
            assertEquals(5, posts2.toList().size());

            //Ensure no duplicates in the two pages
            for(Post post1 : posts1){
                for(Post post2 : posts2){
                    assertNotEquals(post1.getPostId(), post2.getPostId());
                }
            }
        }
    }

    @Nested
    @Sql({"/schema.sql"})
    class searchPosts{
        private void fill_test_data(){
            User user = userRepository.findAll().get(0);
            Category category1 = categoryRepository.findAll().get(0);
            Category category2 = categoryRepository.findAll().get(1);
            Category[] categories = new Category[]{category1, category2};
            String[] titles = new String[]{"Hammer", "Sag", "Drill"};
            Location[] locations = new Location[]{
                    new Location(63.43638306013904,10.422946214675903,"Innherredsveien 60","Trondheim","7067","Norge"),
                    new Location(63.42636950000001,10.4718022,"Tungavegen 26","Trondheim","7047","Norge"),
                    new Location(60.120178104160715,11.480327811508765,"Rådyrvegen 1","Årnes","2150","Norge"),
                    new Location(60.26120815358961,5.067768851914876,"Lyngvegen 3","Skogsvåg","5382","Norge"),
                    new Location(63.794162447769374,11.484832763671875,"Lektor Musums gate 2","Verdal","7650","Norge")
            };

            for(Category category : categories){
                for(String title : titles){
                    for(Location location : locations) {
                        // We must use a copy of the data to make it work

                        Location locationCopy = new Location(location.getLatitude(), location.getLongitude(), location.getAddress(), location.getCity(), location.getPostCode(), location.getCountry());
                        Post post = new Post(title, 40, category, "", locationCopy, user, null, new HashSet<>());
                        postRepository.save(post);
                    }
                }
            }
        }

        @Test
        public void handles_empty_search(){
            fill_test_data();

            //Send empty search request
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("index", "0");
            queryParams.put("size", "20");
            List<Post> posts1 = postService.search(queryParams).toList();
            queryParams.put("size", "10");
            List<Post> posts2 = postService.search(queryParams).toList();

            //Ensure correct response size
            assertEquals(20, posts1.size());
            assertEquals(10, posts2.size());
        }

        @Test
        public void handles_search_by_title() {
            fill_test_data();

            //Search by title
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("index", "0");
            queryParams.put("size", "15");
            queryParams.put("search", "Hammer");
            Page<Post> posts = postService.search(queryParams);
            List<Post> postList = posts.toList();

            assertEquals(10, postList.size());

            //Ensure only title got filtered
            for(Post post : postList){
                assertEquals(post.getTitle(), "Hammer");
            }
        }

        @Test
        public void handles_search_by_title_and_category(){
            fill_test_data();

            //Search by all args
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("index", "0");
            queryParams.put("size", "15");
            queryParams.put("search", "hammer");
            queryParams.put("category", "Tools");
            Page<Post> posts = postService.search(queryParams);

            //Ensure correct response size
            assertEquals(posts.toList().size(), 5);

            //Ensure correct response
            for(Post post : posts) {
                assertEquals(post.getTitle(), "Hammer");
                assertEquals(post.getCategory().getName(), "Tools");
            }
        }

        @Test
        public void handles_distance_search(){
            fill_test_data();

            //Search by locationj
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("index", "0");
            queryParams.put("size", "10");
            queryParams.put("lat", "63.42636950000001");
            queryParams.put("lon", "10.4718022");
            queryParams.put("range", "5000");
            Page<Post> posts = postService.search(queryParams);

            //Ensure correct response size
            assertEquals(10, posts.toList().size());

            //Ensure correct response
            for(Post post : posts) {
                assertEquals("Trondheim", post.getLocation().getCity());
            }
        }

        @Test
        public void handles_invalid_args(){
            fill_test_data();

            //Search by incorrect title
            PostRequest request1 = new PostRequest("Trillebår","");
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("search", "Trillebår");
            Page<Post> posts1 = postService.search(queryParams);

            //Search by incorrect category
            queryParams = new HashMap<>();
            queryParams.put("category", "Leker");

            Page<Post> posts2 = postService.search(queryParams);

            //Ensure all results are empty
            assertEquals(posts1.toList().size(), 0);
            assertEquals(posts2.toList().size(), 0);
        }



    }

}
