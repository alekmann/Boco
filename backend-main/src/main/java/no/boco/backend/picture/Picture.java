package no.boco.backend.picture;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.boco.backend.post.Post;

import javax.persistence.*;

/**
 * The picture class represents a Picture for a {@link Post}.
 */
@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;

    @Column(columnDefinition = ("LONGTEXT"))
    private String file;
    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    protected Picture() {}

    /**
     * Create a new Picture object without attaching it to a post.
     * @param name the filename.
     * @param file the byte array of the file.
     */
    public Picture(String name, String file) {
        this.name = name;
        this.file = file;
    }

    /**
     * Create a new Picture object.
     * @param name the filename.
     * @param file the byte array of the file.
     * @param post the post to attach the picture to.
     */
    public Picture(String name, String file, Post post) {
        this.name = name;
        this.file = file;
        this.post = post;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
