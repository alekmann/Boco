package no.boco.backend.category;

import no.boco.backend.post.Post;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The Category class represents an items category on the marketplace, example: Tools, Electronics, etc.
 * A {@link Post} object will use a Category as a foreign key.
 */
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;

    // Font awesome icon name
    private String icon;

    @OneToMany(mappedBy="category")
    private Set<Post> posts = new HashSet<>();

    protected Category() {}

    /**
     * Create a new Category object.
     * @param name the name of the category.
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * Create a new Category object with font-awesome-icon
     * @param name the name of the category.
     */
    public Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
