package no.boco.backend.organization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.boco.backend.post.Post;
import no.boco.backend.user.User;
import no.boco.backend.userorg.UserOrganization;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Organization  {
    @Id
    private Long orgNum;
    private String orgName;
    private String nickname;
    @Column(columnDefinition = "LONGTEXT")
    private String profilePicture;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.REMOVE)

    private Set<Post> posts = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "organization", cascade = CascadeType.REMOVE)
    private Set<UserOrganization> userOrganizations = new HashSet<>();

    public Organization(Long orgNum, String nickname) {
        this.orgNum = orgNum;
        this.nickname = nickname;
    }

    protected Organization() {

    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Long getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(Long orgNum) {
        this.orgNum = orgNum;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public Set<UserOrganization> getUserOrganizations() {
        return userOrganizations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization)) return false;
        Organization that = (Organization) o;
        return getOrgNum().equals(that.getOrgNum()) && getOrgName().equals(that.getOrgName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrgNum(), getOrgName());
    }

    @JsonIgnoreProperties(value = {"organizations"})
    public Set<User> getEmployees() {
        Set<User> employees = new HashSet<>();
        userOrganizations.forEach((a) -> employees.add(a.getUser()));
        return employees;
    }
}
