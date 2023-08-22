package no.boco.backend.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.boco.backend.location.Location;
import no.boco.backend.organization.Organization;
import no.boco.backend.post.Post;
import no.boco.backend.rental.Rental;
import no.boco.backend.userorg.UserOrganization;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Users of the application
 */
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"posts", "users"})
    private Location location;


    private Date birthDate;
    @Column(columnDefinition = "LONGTEXT")
    private String profilePicture;
    @JsonIgnore
    private Role role;
    @JsonIgnore
    private String activation;

    @JsonIgnoreProperties(value = {"user"})
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<UserOrganization> userOrganizations = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<Rental> rentals = new HashSet<>();

    public User() {
    }

    public User(String email, String password, String firstName, String lastName, Location location, Date birthDate, Role role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.birthDate = birthDate;
        this.role = role;
    }

    public User(String email, String password, String firstName, String lastName, Date birthDate, Role role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.role = role;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return (activation == null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public Set<UserOrganization> getUserOrganizations() {
        return userOrganizations;
    }

    /**
     * Peels a layer from the user/organization connections, allowing for there to follow a list of organizations
     * with the user
     *
     * @return list of organizations the user is in
     */
    @JsonIgnoreProperties(value = {"employees", "posts"})
    public Set<Organization> getOrganizations() {
        Set<Organization> organizations = new HashSet<>();
        userOrganizations.forEach((a) -> organizations.add(a.getOrganization()));
        return organizations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
