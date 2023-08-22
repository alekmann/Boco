package no.boco.backend.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findPostsByUser_IdAndOrganizationIsNull(Long userId, Pageable pageable);
    Page<Post> findPostsByOrganization_OrgNum(Long orgNum, Pageable pageable);

    @Query("SELECT distinct p FROM Post p LEFT JOIN p.rentals AS r ON p.postId = r.post.postId WHERE (:title IS NULL OR lower(p.title) like %:title%) AND (:category IS NULL OR lower(p.category.name) = lower(:category))  AND (:lat IS NULL OR :lon IS NULL OR :range IS NULL OR (MAPDIST(p.location.latitude,:lat,p.location.longitude,:lon) < :range)) AND (:fromDate IS NULL OR :toDate IS NULL OR p.postId NOT IN (SELECT p FROM Post p LEFT JOIN p.rentals as r on p.postId = r.post.postId WHERE r.approved = true AND (:fromDate between r.fromDate and r.toDate) OR (:toDate between r.fromDate and r.toDate)))")
    Page<Post> searchForPosts(
            @Param("title") String title,
            @Param("category") String category,
            @Param("lat") Double latitude,
            @Param("lon") Double longitude,
            @Param("range") Integer range,
            @Param("fromDate") Date from,
            @Param("toDate") Date to,
            Pageable pageable);

}
