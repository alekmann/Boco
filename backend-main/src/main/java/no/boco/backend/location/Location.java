package no.boco.backend.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.boco.backend.post.Post;
import no.boco.backend.user.User;

import javax.persistence.*;

@Entity
public class Location {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
    private double latitude;
    private double longitude;
    private String address;
    private String city;
    private String postCode;
    private String country;

    @OneToOne(mappedBy="location")
    @JsonIgnore
    private Post post;

    @OneToOne(mappedBy="location")
    @JsonIgnore
    private User user;

    public Location(double latitude, double longitude, String address, String city, String postCode, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
        this.postCode = postCode;
        this.country = country;
    }

    protected Location() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Post getPost() {
        return post;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toStringL() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", postCode='" + postCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
