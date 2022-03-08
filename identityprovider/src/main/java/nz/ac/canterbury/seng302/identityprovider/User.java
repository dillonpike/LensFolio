package nz.ac.canterbury.seng302.identityprovider;

import java.util.ArrayList;

public class User {

    private Database database;

    private String username;
    private String password;

    /**
     * Initialises the user with a username and password.
     * @param username Used to login
     * @param password User to login
     */
    public User(Database database, String username, String password) {
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getId() {
        return database.getIdFromDatabase(username);
    }

    public String getFullName() {
        Integer id = this.getId();
        if (id != null) {
            return database.getStringFromDatabase(this.getId(), "fullname");
        } else {
            return "";
        }

    }

    public String getEmail() {
        Integer id = this.getId();
        if (id != null) {
            return database.getStringFromDatabase(this.getId(), "email");
        } else {
            return "";
        }

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        Integer id = this.getId();
        if (id != null) {
            return String.format("User [id=%s, username=%s]", this.getId(), username);
        } else {
            return String.format("User [username=%s]", username);
        }
    }

    /**
     * Checks if the user is in the database and has the right password.
     * @return boolean of if the user is in the database
     */
    public boolean inDatabase() {
        return database.inDatabase(username, password);
    }
}
