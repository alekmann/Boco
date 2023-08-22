package no.boco.backend.picture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for the picture entity.
 */
@RestController
@RequestMapping("/pictures")
public class PictureController {
    @Autowired
    private PictureService pictureService;
    Logger logger = LoggerFactory.getLogger(PictureController.class);

    /**
     * Adds a base64 encoded picture to the database
     * @param picture to be added
     * @param postId id of the post containing the picture
     * @return id of the updated picture
     */
    @PostMapping("/{postId}")
    public ResponseEntity<Long> add(@RequestBody Picture picture, @PathVariable("postId") Long postId) {
        try{
            picture.setPost(pictureService.getPostById(postId));
            return new ResponseEntity<>(pictureService.add(picture), HttpStatus.CREATED);
        } catch (Exception e){
            logger.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets all pictures given a post id
     * @param postId id of the post
     * @return list with all pictures from the post
     */
    @GetMapping("/{postId}")
    public ResponseEntity<List<Picture>> getByPostId(@PathVariable Long postId){
        try{
            return new ResponseEntity<>(pictureService.getAllByPostId(postId), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
