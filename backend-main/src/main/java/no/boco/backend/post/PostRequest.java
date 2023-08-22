package no.boco.backend.post;

import java.time.LocalDate;

/**
 * Request sent when searching for posts.
 */
public class PostRequest {
    private String title;
    private String category;
    private Double latitude;
    private Double longitude;
    private Integer range;
    private String city;
    private String postCode;
    private String country;
    private LocalDate from;
    private LocalDate to;

    public PostRequest(String title, String category, Double latitude, Double longitude, Integer range, LocalDate from, LocalDate to) {
        this.title = title;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.from = from;
        this.to = to;
    }

    public PostRequest(String title, String category) {
        this.title = (title == null) ? "" : title.toLowerCase();
        this.category = (category == null) ? "" : category.toLowerCase();
    }

    public PostRequest(String title, String category, Double latitude, Double longitude, Integer range) {
        this.title = (title == null) ? "" : title.toLowerCase();
        this.category = (category == null) ? "" : category.toLowerCase();
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
    }

    public PostRequest(String title, String category, Double latitude, Double longitude, int range, String city,
                       String postCode, String country) {
        this.title = (title == null) ? "" : title.toLowerCase();
        this.category = (category == null) ? "" : category.toLowerCase();
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.city = city == null ? "" : city.toLowerCase();
        this.postCode = postCode == null ? "": postCode.toLowerCase();
        this.country = country == null ? "": country.toLowerCase();
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getRange() {
        return range;
    }

    public String getCity(){
        return city;
    }
    public String getPostCode(){
        return postCode;
    }
    public String getCountry(){
        return country;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "PostRequest{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", range=" + range +
                ", city='" + city + '\'' +
                ", postCode='" + postCode + '\'' +
                ", country='" + country + '\'' +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
