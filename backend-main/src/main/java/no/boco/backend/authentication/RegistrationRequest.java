package no.boco.backend.authentication;

import no.boco.backend.location.Location;

import java.sql.Date;

/**
 * Used for storing data when a user tries to register
 */
public class RegistrationRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Location location;
    private Date birthDate;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String email, String password, String firstName, String lastName, Location location, Date birthDate) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
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
}

