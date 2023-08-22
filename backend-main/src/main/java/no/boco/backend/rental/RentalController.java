package no.boco.backend.rental;

import no.boco.backend.authentication.CustomAuthenticationException;
import no.boco.backend.post.PostNotFoundException;
import no.boco.backend.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Rental controller class
 */
@RestController
@CrossOrigin
public class RentalController {
    private static final Logger logger = LoggerFactory.getLogger(RentalController.class);

    @Autowired
    private RentalService rentalService;

    /**
     * Method to get all the rentals a user has requested
     * @param approved if the approved param is present, all rentals that the user is a owner of, with the
     *                 given approved value, will be returned
     * @return an iterable of all the rental stored, and it will
     * throw an exception if there is something wrong.
     */
    @GetMapping("/rentals")
    public ResponseEntity<List<Rental>> getRentals(@RequestParam("approved") Optional<Boolean> approved, Authentication authentication){
        try{
            User user = (User) authentication.getPrincipal();
            logger.trace("Getting rentals...");
            if(approved.isPresent()) {
                return new ResponseEntity<>(rentalService.getAllByApproved(approved.get(), user), HttpStatus.OK);
            }
            return new ResponseEntity<>(rentalService.getAll(user), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/theirRentals")
    public ResponseEntity<List<Rental>> getTheirRentals(Authentication authentication){
        try{
            User user = (User) authentication.getPrincipal();
            logger.trace("Getting rentals...");
            return new ResponseEntity<>(rentalService.getAllByOwner(user), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Creates a rental request for the given post with the given rental data
     * @param id id of post
     * @param rental rental rental data
     * @return rental, if it is successfull
     */
    @PostMapping("/posts/{id}/rentals")
    public ResponseEntity<Rental> requestRental(@PathVariable Long id, @RequestBody Rental rental, Authentication authentication) {
        try {
            logger.trace("Creating rental request for post " + id);
            User caller = (User) authentication.getPrincipal();
            return new ResponseEntity<>(rentalService.createRentalRequest(id, rental, caller), HttpStatus.OK);
        } catch (PostNotFoundException | IllegalArgumentException | NoSuchElementException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Changes the approved value of a given rental. This only works if the caller is the creator of the post
     * @param id id of rental
     * @param rental new data
     * @return Updated rental
     */
    @PatchMapping("/rentals/{id}/approved")
    public ResponseEntity<Rental> approvedRentalRequest(@PathVariable Long id, @RequestBody Rental rental, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            return new ResponseEntity<>(rentalService.updateApproved(id, rental.isApproved(), user), HttpStatus.OK);
        } catch (RentalNotFoundException | NoSuchElementException e) {
            logger.trace(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (CustomAuthenticationException e) {
            logger.trace(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Removes a rental request, effectively saying the request is denied
     * @return status code
     */
    @DeleteMapping("/rentals/{id}")
    public ResponseEntity<HttpStatus> deleteRental(@PathVariable Long id, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            rentalService.delete(id, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RentalNotFoundException | NoSuchElementException e) {
            logger.trace(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (CustomAuthenticationException e) {
            logger.trace(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Method to get all the rentals count (not approved rentals) a user has requested
     *
     * @return how many rentals are not approved, and it will
     * throw an exception if there is something wrong.
     */
    @GetMapping("/rentals/count")
    public ResponseEntity<Integer> getRentalsCount(Authentication authentication){
        try{
            User user = (User) authentication.getPrincipal();
            logger.trace("Getting rentals count...");
            return new ResponseEntity<>(rentalService.getAllByApproved(false, user).size(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
