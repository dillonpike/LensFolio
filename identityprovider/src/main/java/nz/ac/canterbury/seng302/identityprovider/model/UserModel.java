package nz.ac.canterbury.seng302.identityprovider.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class UserModel {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String bio;
    private String personalPronouns;

    public UserModel() {}

    public UserModel(String username, String password, String firstName, String middleName, String lastName, String email, String bio, String personalPronouns) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.bio = bio;
        this.personalPronouns = personalPronouns;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

//    public String getNickName() {
//        return nickName;
//    }
//
//    public void setNickName(String nickName) {
//        this.nickName = nickName;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPersonalPronouns() {
        return personalPronouns;
    }

    public void setPersonalPronouns(String personalPronouns) {
        this.personalPronouns = personalPronouns;
    }

    @Override
    public String toString() {
        return "User -> id: " + userId + "  username: " + username;
    }
}
