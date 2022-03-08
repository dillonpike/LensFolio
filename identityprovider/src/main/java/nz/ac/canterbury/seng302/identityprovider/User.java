package nz.ac.canterbury.seng302.identityprovider;

import com.sun.source.tree.ReturnTree;

import java.util.ArrayList;

public class User {

    private Database database;

    private String username;
    private String password;
    private boolean hasBeenAddedToDatabase = false;

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

    /**
     * Adds the user to the database and returns whether it was added correctly.
     * @param fullName Full Name of user
     * @param email Email of user
     * @return Boolean of whether the user was added correctly.
     */
    public boolean addUser(String fullName, String email) {
        boolean wasAdded = database.addUser(username, password, fullName, email);
        if (wasAdded) {
            hasBeenAddedToDatabase = true;
        }
        return wasAdded;
    }

    public String getUsername() {
        return username;
    }

    //public String getPassword() {
    //    return password;
    //}

    public Integer getId() {
        return database.getIdFromDatabase(username);
    }

    /**
     * Gets full name from database using the user id.
     * @return Full name of user
     */
    public String getFullName() {
        Integer id = this.getId();
        if (id != null) {
            return database.getStringFromDatabase(id, "fullname");
        } else {
            return "";
        }

    }

    /**
     * Gets email from database using the user id.
     * @return Email of user
     */
    public String getEmail() {
        Integer id = this.getId();
        if (id != null) {
            return database.getStringFromDatabase(id, "email");
        } else {
            return "";
        }

    }

    /**
     * Set username in the user class and database.
     * @param username New username
     * @return Boolean of whether the username was set correctly. If false, then the old username is still set
     */
    public boolean setUsername(String username) {
        Integer id = this.getId();
        boolean wasSet = database.setStringFromDatabase(id, "username", username);
        if (wasSet) {
            this.username = username;
        }
        return wasSet;
    }

    /**
     * Set password in the user class and database.
     * @param password New password
     * @return Boolean of whether the password was set correctly. If false, then the old password is still set
     */
    public boolean setPassword(String password) {
        Integer id = this.getId();
        boolean wasSet = database.setStringFromDatabase(id, "password", password);
        if (wasSet) {
            this.password = password;
        }
        return wasSet;
    }

    /**
     * Sets a new full name in the database.
     * @param newName New name to change it to
     * @return Boolean of whether the data was successfully changed
     */
    public boolean setFullName(String newName) {
        Integer id = this.getId();
        return database.setStringFromDatabase(id, "fullname", newName);
    }

    /**
     * Sets a new email in the database.
     * @param newEmail New email to change it to
     * @return Boolean of whether the data was successfully changed
     */
    public boolean setEmail(String newEmail) {
        Integer id = this.getId();
        return database.setStringFromDatabase(id, "email", newEmail);
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
