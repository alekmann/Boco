package no.boco.backend.rental;

import no.boco.backend.authentication.CustomAuthenticationException;
import no.boco.backend.post.Post;
import no.boco.backend.post.PostNotFoundException;
import no.boco.backend.post.PostService;
import no.boco.backend.user.MailService;
import no.boco.backend.user.User;
import no.boco.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Rental service class
 */
@Service
public class RentalService{

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    /**
     * Gets a rental by its id. Throws rental not found exception if there is no rental
     *
     * @param id id of rental
     * @return rental, if found
     * @throws RentalNotFoundException
     */
    public Rental getById(Long id) {
        return rentalRepository.findById(id).orElseThrow(() -> new RentalNotFoundException(String.format("Rental %s not found", id)));
    }

    /**
     * Method to get all rentals where user is customer.
     * @param user the owner.
     * @return all rentals found.
     * @throws NoSuchElementException
     */
    public List<Rental> getAll(User user) {
        if(user == null || user.getId() == null || !userService.userExists(user.getId())){
            throw new NoSuchElementException("Could not find given user");
        }
        return rentalRepository.findAllByCustomer(user);
    }

    public List<Rental> getAllByOwner(User user) {
        if(user == null || user.getId() == null || !userService.userExists(user.getId())) {
            throw new NoSuchElementException("Could not find given user");
        }
        return rentalRepository.findAllByPost_User(user);
    }

    /**
     * Get all rentals owned by the given user, filter by approved status.
     * @param approved true or false.
     * @param user the owner.
     * @return all rentals found.
     * @throws NoSuchElementException
     */
    public List<Rental> getAllByApproved(Boolean approved, User user) {
        if(user == null || user.getId() == null || !userService.userExists(user.getId())){
            throw new NoSuchElementException("Could not find given user");
        }
        return rentalRepository.findAllByApprovedAndPost_User(approved, user);
    }

    /**
     * Creates a rental request for the given post.
     * @param postId id of post to rent
     * @param rental rental data
     * @param requester the one who makes the request
     * @return the completed rental request
     * @throws PostNotFoundException
     * @throws NoSuchElementException
     * @throws IllegalArgumentException
     */
    public Rental createRentalRequest(Long postId, Rental rental, User requester) throws PostNotFoundException, IllegalArgumentException {
        Post post = postService.findPostById(postId);
        if(requester == null || requester.getId() == null || !userService.userExists(requester.getId())){
            throw new NoSuchElementException("Could not find user");
        }

        if(rental.getToDate().toLocalDate().isBefore(rental.getFromDate().toLocalDate())) {
            throw new IllegalArgumentException("Invalid rental dates");
        }

        if(isDuplicate(requester.getId(),postId,rental.getFromDate(),rental.getToDate())){
            throw new IllegalArgumentException("This rental is already sent");
        }

        rental.setPost(post);
        rental.setCustomer(requester);

        mailService.sendRentalRequest(rental);

        return rentalRepository.save(rental);
    }


    /**
     * Updates the approved boolean of the rental. This can only be done by the owner
     * @param rentalId id of rental
     * @param approved new value for approved
     * @param user caller of method, this must be the owner for the change to occur
     * @return Update rental
     * @throws RentalNotFoundException
     * @throws NoSuchElementException
     * @throws CustomAuthenticationException
     */
    public Rental updateApproved(Long rentalId, Boolean approved, User user) throws RentalNotFoundException{
        Rental rental = getById(rentalId);
        if(user == null || user.getId() == null || !userService.userExists(user.getId())){
            throw new NoSuchElementException("Could not find given user");
        }
        if(!rental.getOwner().equals(user)) throw new CustomAuthenticationException("Rental requests can only be approved by the owner");
        rental.setApproved(approved);


        mailService.sendRentalRequestAnswer(rental);

        if(rental.isApproved()) {
            return rentalRepository.save(rental);
        } else {
            delete(rental.getRentalId(), user);
            return rental;
        }
    }

    /**
     * Deletes the rental with the given is either the requester or the owner of the post
     * @param id id of rental
     * @param caller caller of method
     * @throws RentalNotFoundException
     * @throws NoSuchElementException
     * @throws CustomAuthenticationException
     */
    public void delete(Long id, User caller) throws RentalNotFoundException{
        Rental rental = getById(id);
        if(caller == null || caller.getId() == null || !userService.userExists(caller.getId())){
            throw new NoSuchElementException("Could not find given user");
        }
        if(!rental.getOwner().equals(caller) && !rental.getCustomer().equals(caller)) {
            throw new CustomAuthenticationException("Rental requests can only be deleted by the requester or the owner");
        }
        rentalRepository.delete(rental);
    }

    /**
     * Check if this rental info presents in the database (duplicate rental request from the same user)
     * @param customer_id requester id
     * @param post_postId post id
     * @param fromDate from date
     * @param toDate to date
     * @return true or false
     */
    public boolean isDuplicate(Long customer_id, Long post_postId, Date fromDate, Date toDate){
         Optional<Rental> rental = rentalRepository.findByCustomer_IdAndPost_PostIdAndFromDateAndToDate(customer_id, post_postId, fromDate, toDate);
         return rental.isPresent();
    }
}
