package no.boco.backend.post;

import no.boco.backend.rental.Range;
import no.boco.backend.rental.Rental;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class PostTest {

    @Test
    @DisplayName("Ranges are correctly calculated")
    public void calculateRanges() {
        Post post = new Post();
        post.setPostId(1L);
        for(int i = 0; i < 3; i++) {
            Rental rental = new Rental();
            rental.setAmount(1);
            rental.setFromDate(Date.valueOf("2020-01-" + (i + 1)));
            rental.setToDate(Date.valueOf("2020-02-" + (i + 1)));
            rental.setPost(post);
            post.getRentals().add(rental);
        }

        System.out.println(Arrays.toString(post.getRentals().toArray()));

        List<Range> ranges = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            ranges.add(new Range(
                    LocalDate.of(2020, 1, i + 1),
                    LocalDate.of(2020, 2, i + 1),
                    i + 1
            ));

        }
//        ranges.add(new Range(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1), 1));
//        ranges.add(new Range(LocalDate.of(2020, 1, 2), LocalDate.of(2020, 2, 2), 1));
//        ranges.add(new Range(LocalDate.of(2020, 1, 2), LocalDate.of(2020, 2, 2), 1));
//        ranges.add(new Range(LocalDate.of(2020, 1, 3), LocalDate.of(2020, 2, 3), 1));
//        ranges.add(new Range(LocalDate.of(2020, 1, 3), LocalDate.of(2020, 2, 3), 1));
//        ranges.add(new Range(LocalDate.of(2020, 1, 3), LocalDate.of(2020, 2, 3), 1));


//        System.out.println(Arrays.toString(post.calculate(new ArrayList<>(), ranges).toArray()));
    }
}
