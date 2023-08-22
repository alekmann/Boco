package no.boco.backend.picture;

import no.boco.backend.post.Post;
import no.boco.backend.post.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class for the Picture entity
 */
@Service
public class PictureService {
    @Autowired
    private  PictureRepository pictureRepository;

    @Autowired
    private PostService postService;

    /**
     * Get the first Picture for a Post, to be used as a thumbnail.
     * @param postId the PostId for the Post.
     * @return the first Picture object found.
     */
    public Picture getFirstByPostId(Long postId){
        return pictureRepository.findFirstByPost_PostId(postId);
    }

    /**
     * Get all Pictures for a post.
     * @param postId the PostId for the Post.
     * @return a List of all Pictures found.
     */
    public List<Picture> getAllByPostId(Long postId){
        return pictureRepository.findPicturesByPost_PostId(postId);
    }

    /**
     * Save a Picture to the database, attaching it to its Post.
     * @param picture the Picture object to be saved, with a defined Post field.
     * @return the Id of the new Picture object.
     */
    public Long add(Picture picture){
        return pictureRepository.save(picture).getId();
    }

    /**
     * Delete a Picture from the database.
     * @param pictureId the PictureId of the Picture to delete.
     * @throws NoSuchElementException if the Picture could not be found.
     */
    public void delete(Long pictureId) throws NoSuchElementException{
        if(pictureRepository.existsById(pictureId)){
            pictureRepository.deleteById(pictureId);
        }else{
            throw new NoSuchElementException("Could not find an image with the given Id");
        }
    }

    /**
     * Use post service to return a post with a given id
     * @param id post iod
     * @return post with the id
     */
    public Post getPostById(Long id){
        return postService.findPostById(id);
    }
}
