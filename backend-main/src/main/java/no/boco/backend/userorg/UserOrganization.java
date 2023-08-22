package no.boco.backend.userorg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.boco.backend.organization.Organization;
import no.boco.backend.post.Post;
import no.boco.backend.user.User;

import javax.persistence.*;

/**
 * Entity representing the many to many table UserOrganization. {@link Post} {@link Organization}
 */
@Entity
public class UserOrganization {

    @JsonIgnore
    @EmbeddedId
    private UserOrganizationKey id = new UserOrganizationKey(); // Must be initialized

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties(value = {"organizations"})
    private User user;

    @ManyToOne
    @MapsId("orgNum")
    @JoinColumn(name = "org_num")
    @JsonIgnoreProperties(value = {"posts", "employees"})
    private Organization organization;

    private OrganizationRole organizationRole;

    public UserOrganization() {}

    public UserOrganization(User user, Organization organization, OrganizationRole organizationRole) {
        this.user = user;
        this.organization = organization;
        this.organizationRole = organizationRole;
    }

    public UserOrganizationKey getId() {
        return id;
    }

    public void setId(UserOrganizationKey id) {
        this.id = id;
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

    public OrganizationRole getOrganizationRole() {
        return organizationRole;
    }

    public void setOrganizationRole(OrganizationRole organizationRole) {
        this.organizationRole = organizationRole;
    }

    @Override
    public String toString() {
        return "UserOrganization{" +
                "user=" + user.getEmail() +
                ", organization=" + organization.getNickname() +
                ", organizationRole=" + organizationRole +
                '}';
    }
}
