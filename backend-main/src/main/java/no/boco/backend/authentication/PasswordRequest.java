package no.boco.backend.authentication;

/**
 * Used when updating password
 */
public class PasswordRequest {
    private String oldPassword; //push
    private String newPassword;
    private String repeatedPassword;

    public PasswordRequest(String oldPassword, String newPassword, String repeatedPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.repeatedPassword = repeatedPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }

    /**
     * Checks that the new password is not null and that it equals the repeated version of itself
     * @return validity of new password
     */
    public boolean isNewPasswordValid() {
        return newPassword != null && newPassword.equals(repeatedPassword);
    }

}
