package no.boco.backend.rental;


import no.boco.backend.authentication.CustomAuthenticationException;
import no.boco.backend.category.Category;
import no.boco.backend.category.CategoryRepository;
import no.boco.backend.organization.OrganizationRepository;
import no.boco.backend.post.Post;
import no.boco.backend.post.PostNotFoundException;
import no.boco.backend.post.PostRepository;
import no.boco.backend.user.User;
import no.boco.backend.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RentalIntegrationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    RentalRepository rentalRepository;
    @Autowired
    RentalService rentalService;

    private User user;
    private User user1;
    private User user2;
    private Category category;
    private Category category1;
    private Category category2;
    private Post post;
    private Post post1;
    private Post post2;
    private Long postID;
    private Long postID1;
    private Long postID2;
    private Rental rental;
    private Rental rental1;
    private Rental rental2;



    @BeforeEach
    void fillData(){
        System.out.println("Starting tests...");
        user = new User("test@test.test", "pass",
                "te", "test",
                null, Date.valueOf("1990-10-10"),
                null);
        userRepository.save(user);
        category = new Category("Car");
        categoryRepository.save(category);
        post = new Post("Audi", 4000,
                category, "Description about",
                null, user, null, null);
        postRepository.save(post);
        user1 = new User("test1User1@test.test", "password1",
                "test1", "test1",
                null, Date.valueOf("2000-10-10"),
                null);
        userRepository.save(user1);
        category1 = new Category("Movies");
        categoryRepository.save(category1);
        post1 = new Post("Movie", 500,
                category1, "A new movie",
                null, user1, null, null);
        postRepository.save(post1);
        user2 = new User("test2User2@test.test", "password2",
                "test2", "test2",
                null, Date.valueOf("1999-12-10"),
                null);
        userRepository.save(user2);
        category2 = new Category("Games");
        categoryRepository.save(category2);
        post2 = new Post("Games", 799,
                category2, "A new game",
                null, user2, null, null);
        postRepository.save(post2);
        rental = new Rental(Date.valueOf("2022-10-22"), Date.valueOf("2022-10-30"),post, user, true);
        rental1 = new Rental(Date.valueOf("2022-11-22"), Date.valueOf("2022-11-30"),post1, user1, false);
        rental2 = new Rental(Date.valueOf("2022-12-22"), Date.valueOf("2022-12-30"),post2, user2, true);
        postID = postRepository.findAll().get(0).getPostId();
        postID1 = postRepository.findAll().get(1).getPostId();
        postID2 = postRepository.findAll().get(2).getPostId();

    }

    @AfterEach
    void reset(){
        rentalRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Nested
    class getById{
        @Test
        public void finds_correct_rental(){
            try {
                rental = rentalRepository.save(rental);
                Rental result = rentalService.getById(rental.getRentalId());

                assertEquals(result.getRentalId(), rental.getRentalId());
            }catch(RentalNotFoundException e){
                fail();
            }
        }

        @Test
        public void throws_error_at_wrong_id(){
            try{
                rental = rentalRepository.save(rental);
                Long wrongId = rental.getRentalId() + 1;

                Rental result = rentalService.getById(wrongId);
                fail();
            }catch(RentalNotFoundException e){
                //pass test
            }
        }
    }

    @Nested
    class getAll{
        @Test
        public void finds_all_rentals_for_post_owner(){
            //Save three rentals each with different owners
            rental = rentalRepository.save(rental);
            rental1 = rentalRepository.save(rental1);
            rental2 = rentalRepository.save(rental2);

            //Verify that only rentals from 'user' gets sent
            try {
                List<Rental> rentals = rentalService.getAll(user);
                for(Rental result : rentals){
                    assertEquals(user.getEmail(), result.getOwner().getEmail());
                }
            }catch(NoSuchElementException e){
                fail();
            }
        }

        @Test
        public void handles_wrong_user(){
            //Create new user and dont save
            User wrong = new User("wrong@user.com", "password2", "Wrong", "User", null, new Date(1999-12-10), null);

            //Try to fetch rentals from 'wrong'
            try {
                rentalService.getAll(wrong);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }
    }

    @Nested
    class getAllByApproved{
        @Test
        public void filters_on_approved_and_user(){
            rental.setApproved(true);
            rental1.setApproved(false);
            rental = rentalRepository.save(rental);
            rental1 = rentalRepository.save(rental1);

            List<Rental> approved = rentalService.getAllByApproved(true, user);
            List<Rental> not_approved = rentalService.getAllByApproved(false, user1);

            for(Rental result : approved){
                assertTrue(result.isApproved());
                assertEquals(user.getEmail(), result.getOwner().getEmail());
            }
            for(Rental result : not_approved){
                assertFalse(result.isApproved());
                assertEquals(user1.getEmail(), result.getOwner().getEmail());
            }
        }

        @Test
        public void handles_wrong_user(){
            //Create new user and dont save
            User wrong = new User("wrong@user.com", "password2", "Wrong", "User", null, new Date(1999-12-10), null);

            try {
                rentalService.getAllByApproved(true, wrong);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }
    }

    @Nested
    class createRentalRequest{
        @Test
        public void method_saves_rental(){
            Rental result = rentalService.createRentalRequest(rental.getPost().getPostId(), rental, user);
            Long resultId = rentalRepository.findById(result.getRentalId()).get().getRentalId();

            assertTrue(rentalRepository.existsById(resultId));
        }

        @Test
        public void handles_non_existing_post(){
            postRepository.deleteAll();

            try{
                Rental result = rentalService.createRentalRequest(1L, rental, user);
                fail();
            }catch(PostNotFoundException e){
                //pass test;
            }
        }

        @Test
        public void handles_non_existing_user(){
            //Create new user and dont save
            User wrong = new User("wrong@user.com", "password2", "Wrong", "User", null, new Date(1999-12-10), null);

            try {
                Rental result = rentalService.createRentalRequest(rental.getPost().getPostId(), rental, wrong);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }

        @Test
        public void handles_backwards_date_range(){
            Rental wrong = new Rental(Date.valueOf("2022-10-30"), Date.valueOf("2022-10-01"), post, user, false);

            try{
                Rental result = rentalService.createRentalRequest(wrong.getPost().getPostId(), wrong, user);
                fail();
            }catch(IllegalArgumentException e){
                //pass test
            }
        }
    }

    @Nested
    class updateApproved{
        @Test
        public void method_saves_change(){
            rental.setApproved(false);
            rental = rentalRepository.save(rental);
            assertFalse(rental.isApproved());

            rental = rentalService.updateApproved(rental.getRentalId(), true, rental.getOwner());
            assertTrue(rental.isApproved());
        }

        @Test
        public void handles_non_existing_rental(){
            assertTrue(rentalRepository.findAll().isEmpty());
            try{
                Rental result = rentalService.updateApproved(1L, true, user);
                fail();
            }catch(RentalNotFoundException e){
                //pass test
            }
        }

        @Test
        public void handles_non_existing_user(){
            rental = rentalRepository.save(rental);

            //Create new user and dont save
            User wrong = new User("wrong@user.com", "password2", "Wrong", "User", null, new Date(1999-12-10), null);

            try{
                Rental result = rentalService.updateApproved(rental.getRentalId(), true, wrong);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }

        @Test
        public void rejects_call_from_non_owner(){
            rental = rentalRepository.save(rental);
            assertEquals(user.getEmail(), rental.getOwner().getEmail());

            try{
                Rental result = rentalService.updateApproved(rental.getRentalId(), true, user1);
                fail();
            }catch(CustomAuthenticationException e){
                //pass test
            }
        }
    }

    @Nested
    class delete{
        @Test
        public void deletes_given_rental(){
            rental = rentalRepository.save(rental);

            assertTrue(rentalRepository.existsById(rental.getRentalId()));
            rentalService.delete(rental.getRentalId(), rental.getOwner());
            assertFalse(rentalRepository.existsById(rental.getRentalId()));
        }

        @Test
        public void handles_non_existing_rental(){
            rentalRepository.deleteAll();

            try{
                rentalService.delete(1L, user);
                fail();
            }catch(RentalNotFoundException e){
                //pass test
            }
        }

        @Test
        public void handles_non_existing_user(){
            rental = rentalRepository.save(rental);

            //Create new user and dont save
            User wrong = new User("wrong@user.com", "password2", "Wrong", "User", null, new Date(1999-12-10), null);

            try{
                rentalService.delete(rental.getRentalId(), wrong);
                fail();
            }catch(NoSuchElementException e){
                //pass test
            }
        }

        @Test
        public void rejects_non_owner(){
            rental = rentalRepository.save(rental);
            assertEquals(user.getEmail(), rental.getOwner().getEmail());

            try{
                rentalService.delete(rental.getRentalId(), user1);
                fail();
            }catch(CustomAuthenticationException e){
                //pass test
            }
        }
    }

    @Test
    void createRentals(){
        System.out.println("Test1: Creating a rental.");

        Rental rentalTest = rentalService.createRentalRequest(postID, rental, user);
        assertTrue(rentalTest.isApproved());

    }


    @Test
    void  CreatingMoreRentals(){
        System.out.println("Test2: creating more rentals and checking if all the rentals are stored.");

        Rental rentalTest1 = rentalService.createRentalRequest(postID1, rental1, user1);
        Rental rentalTest2 = rentalService.createRentalRequest(postID2, rental2, user2);
        Assertions.assertEquals(2, rentalRepository.findAll().size());


    }

    @Test
    void checkingAllApproved(){
        System.out.println("Test3: Checking how many approved rentals.");
        Rental rentalCreate = rentalService.createRentalRequest(postID, rental, user);
        Rental renatlCreate1 = rentalService.createRentalRequest(postID1, rental1, user1);
        Rental renatlCreate2 = rentalService.createRentalRequest(postID2, rental2, user2);
        Assertions.assertEquals(2, rentalRepository.
                findAll().
                stream().
                filter(p->p.isApproved()==true).
                count());

    }

    @Test
    void getByID(){
        System.out.println("Test4: Getting rental by user id.");
        Rental rentalCreate = rentalService.createRentalRequest(postID, rental, user);
        Long rentalCreateID = rentalRepository.findAll().get(0).getRentalId();
        Assertions.assertEquals(rentalCreate.getRentalId(), rentalService.getById(rentalCreateID).getRentalId());

    }
    @Test
    void updateApproved(){
        System.out.println("Test5: Updating the rental approval.");
        Rental rentalCreate1 = rentalService.createRentalRequest(postID1, rental1, user1);
        Long rentalCreateID1 = rentalRepository.findAll().get(0).getRentalId();
        boolean rentalApprovalUpdate = rentalRepository.findAll().get(0).isApproved();
        Rental rentalCreateUpdated = rentalService.updateApproved(rentalCreateID1, true, user1);
        Assertions.assertFalse(rentalCreateUpdated.isApproved()==rentalApprovalUpdate);


    }

    @Test
    void getAllByOwner() {
        System.out.println("Test6: getting rental owners.");
        Rental rentalCreate1 = rentalService.createRentalRequest(postID1, rental1, user1);
        Rental rentalCreate2 = rentalService.createRentalRequest(postID2, rental2, user2);
        List<Rental> usersRentals = rentalService.getAllByOwner(user1);
        User user = usersRentals.get(0).getPost().getUser();
        assertEquals(1, usersRentals.size());
    }


}
