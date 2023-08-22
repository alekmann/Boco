package no.boco.backend.userorg;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Class for composite keys used in the UserOrganization table
 */
@Embeddable
public class UserOrganizationKey implements Serializable {
    private Long userId;
    private Long orgNum;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(Long orgNum) {
        this.orgNum = orgNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserOrganizationKey that = (UserOrganizationKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(orgNum, that.orgNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, orgNum);
    }
}

