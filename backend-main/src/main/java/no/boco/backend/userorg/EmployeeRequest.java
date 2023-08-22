package no.boco.backend.userorg;

/**
 * Class representing the requesting being sent when an employee is added to an organization.
 */
public class EmployeeRequest {
    private String email;
    private Long orgNum;
    private OrganizationRole role;

    public EmployeeRequest(String email, Long orgNum, OrganizationRole role) {
        this.email = email;
        this.orgNum = orgNum;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public Long getOrgNum() {
        return orgNum;
    }

    public OrganizationRole getRole() {
        return role;
    }
}
