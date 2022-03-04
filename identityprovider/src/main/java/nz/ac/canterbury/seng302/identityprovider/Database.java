package nz.ac.canterbury.seng302.identityprovider;

import java.util.ArrayList;

public class Database {

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

    /**
     * Checks if the user is in the database and has the right password.
     * @return boolean of if the user is in the database
     */
    public boolean inDatabase(String username, String password) {
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
