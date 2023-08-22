package no.boco.backend.picture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

    Picture findFirstByPost_PostId(Long postId);
    List<Picture> findPicturesByPost_PostId(Long postId);
}
