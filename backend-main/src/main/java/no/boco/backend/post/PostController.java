package no.boco.backend.post;

import no.boco.backend.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/posts")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<String> addPost(@RequestBody Post post, Authentication authentication){
       try {
           logger.trace("Saving post " + post);
           User user = (User) authentication.getPrincipal();
           return new ResponseEntity<>(String.valueOf(postService.add(post, user)), HttpStatus.CREATED);
        } catch(NoSuchElementException e){
            logger.info("No such element exception");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(IllegalAccessException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping
    public ResponseEntity<Post> editPost(@RequestBody Post post, Authentication authentication){
        logger.trace("Editing post " + post);
        User user = (User) authentication.getPrincipal();
        try{
            return new ResponseEntity<>(postService.edit(post, user), HttpStatus.OK);
        }catch(NoSuchElementException e){
            logger.trace(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch(IllegalAccessException e){
            logger.trace(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, Authentication authentication){
        logger.trace("Deleting post " + postId);
        User user = (User) authentication.getPrincipal();
        try {
            postService.delete(postId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch(IllegalAccessException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id) {
        try {
            logger.trace("Fetching post " + id);
            return new ResponseEntity<>(postService.findPostById(id), HttpStatus.OK);
        } catch (PostNotFoundException e) {
            logger.trace(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{index}/{size}")
    public ResponseEntity<Page<Post>> getPosts(@PathVariable int index, @PathVariable int size){
        logger.trace("Fetching posts on page " + index + "/" + size);
        try {
            return new ResponseEntity<>(postService.getPosts(index, size), HttpStatus.OK);
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{userId}/{index}/{size}")
    public ResponseEntity<Page<Post>> getUserPosts(@PathVariable Long userId, @PathVariable int index, @PathVariable int size){
        logger.trace("Fetching posts for user " + userId + " on page " + index + "/" + size);
        try {
            return new ResponseEntity<>(postService.getByUser(index, size, userId), HttpStatus.OK);
        }catch(IllegalArgumentException | NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/org/{orgNum}/{index}/{size}")
    public ResponseEntity<Page<Post>> getOrgPosts(@PathVariable Long orgNum, @PathVariable int index, @PathVariable int size){
        logger.trace("Fetching posts for organization " + orgNum + " on page " + index + "/" + size);
        try {
            return new ResponseEntity<>(postService.getByOrganization(index, size, orgNum), HttpStatus.OK);
        }catch(IllegalArgumentException | NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Post>> test(@RequestParam Map<String, String> queryParams) {
        try {
            logger.trace("Fetching post based on request search");
            return new ResponseEntity<>(postService.search(queryParams), HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
