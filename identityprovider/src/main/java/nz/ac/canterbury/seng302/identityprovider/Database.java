package nz.ac.canterbury.seng302.identityprovider;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

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

    public Database() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
            System.out.println("Connected to database");
            // Creates the table
            //conn.prepareStatement("CREATE TABLE userTable (Id int NOT NULL, Username VARCHAR(30) NOT NULL, Password VARCHAR(30) NOT NULL);").execute();
            // Hardcode a user
            //conn.createStatement().execute("INSERT INTO userTable VALUES (1, 'database', 'database');");
            //System.out.println("Inserted");



            //Currently running
            conn.prepareStatement("DROP TABLE IF EXISTS userTable;").execute();
            conn.prepareStatement("CREATE TABLE userTable (Id int NOT NULL, Username VARCHAR(30) NOT NULL, Password VARCHAR(30) NOT NULL);").execute();

            //Insert some values
            conn.createStatement().execute("INSERT INTO userTable VALUES (1, 'database1', 'database1p');");
            conn.createStatement().execute("INSERT INTO userTable VALUES (2, 'database2', 'database2p');");
            conn.createStatement().execute("INSERT INTO userTable VALUES (3, 'database3', 'database3p');");

            //Get data back
            ResultSet allTable = conn.createStatement().executeQuery("SELECT * FROM userTable");
            System.out.println(allTable);

            //Iterate through ResultSet to get data
            while (allTable.next()) {
                System.out.println("ID: " + allTable.getString("ID") + " Username: " + allTable.getString("Username") + " Password: " + allTable.getString("password"));
            }

            System.out.println("Line Run");

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


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
