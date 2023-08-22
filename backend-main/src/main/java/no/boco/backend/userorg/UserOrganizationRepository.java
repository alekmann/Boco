package no.boco.backend.userorg;

import no.boco.backend.organization.Organization;
import no.boco.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UserOrganizationKey> {
    List<UserOrganization> findAllByOrganization(Organization organization);
    List<UserOrganization> findAllByUser(User user);
    Optional<UserOrganization> getByUserAndOrganization(User user, Organization org);

    @Query("SELECT u FROM UserOrganization u WHERE u.organization.orgNum = :orgNum AND LOWER(CONCAT(CONCAT(u.user.firstName,' '),u.user.lastName)) LIKE %:name%")
    List<UserOrganization> searchByOrgAndFullName(@Param("orgNum") Long orgNum, @Param("name") String name);
}
