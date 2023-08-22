package no.boco.backend.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.boco.backend.category.Category;
import no.boco.backend.location.Location;
import no.boco.backend.organization.Organization;
import no.boco.backend.picture.Picture;
import no.boco.backend.rental.Range;
import no.boco.backend.rental.Rental;
import no.boco.backend.user.User;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * The Post class represents a listing of an item to be borrowed.
 */

@Entity
public class Post {
    @Id
    @GeneratedValue
    private Long postId;

    private String title;
    private Integer pricePerDay;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;
    private Date createdDate;
    private Integer inventory = 1;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties(value = {"organizations", "posts"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "org_num")
    @JsonIgnoreProperties(value = {"employees", "posts"})
    private Organization organization;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Picture> pictures = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"posts", "users"})
    private Location location;

    @JsonIgnore
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<Rental> rentals = new HashSet<>();

    protected Post() {}

    public Post(String title, Integer pricePerDay, Category category, String description, Location location, User user, Organization organization, Set<Picture> pictures) {
        this.title = title;
        this.pricePerDay = pricePerDay;
        this.category = category;
        this.description = description;
        this.location = location;
        this.user = user;
        this.organization = organization;
        this.pictures = pictures;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Integer pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }

    public Set<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(Set<Picture> pictures) {
        if(this.pictures == null) {
            return;
        }
        this.pictures.clear();
        for(Picture picture : pictures) {
            picture.setPost(this);
            this.pictures.add(picture);
        }
    }

    public Set<Rental> getRentals() {
        return rentals;
    }

    /**
     * Calculates ranges of dates that the object is unavailable
     *
     * @return list of unavailable ranges of dates
     */
    public List<Range> getUnavailable() {
        List<Range> ranges = new ArrayList<>();
        Map<LocalDate, Integer> unavailableMap = new HashMap<>();
        for(Rental rental : rentals) {
            if(rental.isApproved() && rental.getToLocalDate().isAfter(LocalDate.now())) {
                for(LocalDate day = rental.getFromLocalDate(); day.compareTo(rental.getToLocalDate()) <= 0; day = day.plusDays(1)) {
                    if(unavailableMap.containsKey(day)) {
                        int amount = unavailableMap.get(day) + rental.getAmount();
                        unavailableMap.put(day, amount);
                    }
                    else {
                        unavailableMap.put(day, rental.getAmount());
                    }
                }
            }
        }
        unavailableMap.forEach((key, value) -> ranges.add(new Range(key, key.plusDays(1), value)));
        return ranges;
    }
}
