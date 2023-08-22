package no.boco.backend.rental;

import no.boco.backend.organization.Organization;
import no.boco.backend.post.Post;
import no.boco.backend.user.User;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Rental class
 */
@Entity
public class Rental {
    @Id
    @GeneratedValue
    private Long rentalId;
    private Date fromDate;
    private Date toDate;
    private Integer totalPrice;
    private Integer amount = 1;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;
    @ManyToOne
    @JoinColumn(name = "org_num", nullable = true)
    private Organization organizationCustomer; // If this field is populated, there is an organization that is renting
    private boolean approved = false;

    public Rental() {

    }

    public Rental(Date fromDate, Date toDate, Post post, User customer, boolean approved) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.post = post;
        this.customer = customer;
        this.approved = approved;
    }

    // Accessor-methods
    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public LocalDate getFromLocalDate() {
        return fromDate.toLocalDate();
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public LocalDate getToLocalDate() {
        return toDate.toLocalDate();
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Organization getOrganizationCustomer(){
        return organizationCustomer;
    }

    public void setOrganizationCustomer(Organization organizationCustomer){
        this.organizationCustomer = organizationCustomer;
    }

    /**
     * Gets the original owner of the product in hand
     *
     * @return lender
     */
    public User getOwner() {
        return post.getUser();
    }

    @Override
    public String toString() {
        return "Rental{" +
//                "rentalId=" + rentalId +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
//                ", customer=" + customer.getId() +
                ", approved=" + approved +
                ", amount=" + amount+
                '}';
    }
}
