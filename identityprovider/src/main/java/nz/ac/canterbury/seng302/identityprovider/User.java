package nz.ac.canterbury.seng302.identityprovider;

import java.util.ArrayList;

public class User {

    Database database = new Database();

    String username;
    String password;

    /**
     * Initialises the user with a username and password.
     * @param username Used to login
     * @param password User to login
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Checks if the user is in the database and has the right password.
     * @return boolean of if the user is in the database
     */
    public boolean inDatabase() {
        return database.inDatabase(username, password);
    }
}
