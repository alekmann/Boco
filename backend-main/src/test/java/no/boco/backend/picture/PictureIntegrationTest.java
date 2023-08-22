package no.boco.backend.picture;

import no.boco.backend.post.Post;
import no.boco.backend.post.PostRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PictureIntegrationTest {
    @Autowired
    PictureService pictureService;

    @Autowired
    PictureRepository pictureRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void fillTestData() {
        User user = new User("ola@normann.no", "abc", "Ola", "Normann", null,
                Date.valueOf("2001-01-01"), null);
        userRepository.save(user);
        user = userRepository.findByEmail("ola@normann.no").orElseThrow(() -> new UsernameNotFoundException("Error getting user"));

        Post post = new Post("Hammer", 80, null, "En veldig fin hammer", null, user, null, null);
        postRepository.save(post);
    }

    @AfterEach
    public void cleanTestData(){
        postRepository.deleteAll();
        userRepository.deleteAll();
        pictureRepository.deleteAll();
    }

    @Test
    @DisplayName("Add picture to post")
    public void addPictureToPost() {
        // Empty
        assertTrue(pictureRepository.findAll().isEmpty());

        Post post = postRepository.findAll().get(0);
        Picture picture = new Picture();
        picture.setPost(post);
        picture.setName("My picture");
        picture.setFile("image");
        pictureService.add(picture);

        // Validate that the data was added
        assertFalse(pictureRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Get all pictures from a post")
    public void getAllPicturesFromPost() {
        assertTrue(pictureRepository.findAll().isEmpty());

        Post post = postRepository.findAll().get(0);
        Picture picture1 = new Picture();
        picture1.setPost(post);
        picture1.setName("My picture");
        picture1.setFile("image");

        Picture picture2 = new Picture();
        picture2.setPost(post);
        picture2.setName("My other picture");
        picture2.setFile("another image");

        pictureService.add(picture1);
        pictureService.add(picture2);

        assertEquals(2, pictureService.getAllByPostId(post.getPostId()).size());

    }

    @Test
    @DisplayName("Get first picture from a post")
    public void getFirstPictureFromPost() {
        assertTrue(pictureRepository.findAll().isEmpty());

        Post post = postRepository.findAll().get(0);
        Picture picture1 = new Picture();
        picture1.setPost(post);
        picture1.setName("My picture");
        picture1.setFile("image");

        Picture picture2 = new Picture();
        picture1.setPost(post);
        picture1.setName("My other picture");
        picture1.setFile("another image");

        pictureService.add(picture1);
        pictureService.add(picture2);

        assertEquals(picture1.getFile(), pictureService.getFirstByPostId(post.getPostId()).getFile());
    }


}
