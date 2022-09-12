package nz.ac.canterbury.seng302.identityprovider.model;

import com.google.protobuf.Timestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
public class UserModel implements Serializable {
    @Id
    private int userId;

    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String email;
    private String bio;
    private String personalPronouns;
    private Timestamp dateAdded;
    private String photoDirectory;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_to_role",
            joinColumns =
                    @JoinColumn(name = "User_Id"),
            inverseJoinColumns =
                    @JoinColumn(name = "Role_Id")
    )
    private Set<Roles> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_to_group",
            joinColumns =
            @JoinColumn(name = "User_Id"),
            inverseJoinColumns =
            @JoinColumn(name = "group_id")
    )
    private Set<GroupModel> groups = new HashSet<>();
    public void addGroup(GroupModel groupModel) {
        this.groups.add(groupModel);
    }


    public void addRoles(Roles role) {
        this.roles.add(role);
    }

    public void deleteRole(Roles role) {
        Set<Roles> filteredSet = new HashSet<>();
        for(Roles userRole: roles) {
            if (!userRole.getRoleName().equals(role.getRoleName())) {
                filteredSet.add(userRole);
            }
        }
        roles = filteredSet;
    }

    public Set<Roles> getRoles() {
        return roles;
    }
    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }
    public UserModel() {}

    public UserModel(String username, String password, String firstName, String middleName, String lastName, String nickname, String email, String bio, String personalPronouns) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.email = email;
        this.bio = bio;
        this.personalPronouns = personalPronouns;
        Instant time = Instant.now();
        this.dateAdded = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).build();
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

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

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDateAddedString() {
        if (dateAdded != null) {
            Date date = new Date(dateAdded.getSeconds() * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            return dateFormat.format(date);
        } else {
            return null;
        }
    }

    public void setPhotoDirectory(String photoDirectory) {
        this.photoDirectory = photoDirectory;
    }

    public String getPhotoDirectory() {
        return photoDirectory;
    }

    @Override
    public String toString() {
        return "User -> id: " + userId + "  username: " + username;
    }

    public Set<GroupModel> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupModel> groups) {
        this.groups = groups;
    }
}
