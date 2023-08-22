package no.boco.backend.rental;

import no.boco.backend.organization.Organization;
import no.boco.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Rental repository class
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByCustomer(User customer);
    List<Rental> findAllByPost_User(User owner);
    List<Rental> findAllByApprovedAndPost_User(boolean approved, User user);
    Optional<Rental> findByCustomer_IdAndPost_PostIdAndFromDateAndToDate(Long customer_id, Long post_postId, Date fromDate, Date toDate);
}
