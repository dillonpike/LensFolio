package nz.ac.canterbury.seng302.identityprovider;

import java.util.ArrayList;

public class User {

    Database database = new Database();

    static Integer id_count = 0;
    Integer id;
    String username;
    String password;

    /**
     * Initialises the user with a username and password.
     * @param username Used to login
     * @param password User to login
     */
    public User(String username, String password) {
        this.id = id_count;
        id_count++;
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
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("student [id=%s, username=%s]", id, username);
    }

    /**
     * Checks if the user is in the database and has the right password.
     * @return boolean of if the user is in the database
     */
    public boolean inDatabase() {
        return database.inDatabase(username, password);
    }
}
