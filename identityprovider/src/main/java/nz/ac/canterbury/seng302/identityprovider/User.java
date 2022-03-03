package nz.ac.canterbury.seng302.identityprovider;

import java.util.ArrayList;

public class User {

    /* Hardcoded users */
    public static ArrayList<ArrayList<String>> database = new ArrayList<>() {
        {
            add(new ArrayList<>() {
                {
                    add("admin");
                    add("admin");
                }
            });
            add(new ArrayList<>() {
                {
                    add("student");
                    add("student");
                }
            });
            add(new ArrayList<>() {
                {
                    add("teacher");
                    add("teacher");
                }
            });
        }
    };

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
        boolean isInDatabase = false;
        for (ArrayList<String> users : database) {
            if (username.equals(users.get(0)) && password.equals(users.get(1))) {
                isInDatabase = true;
                break;
            }
        }
        return isInDatabase;
    }
}
