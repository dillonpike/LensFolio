package nz.ac.canterbury.seng302.identityprovider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Database {

    private Connection conn;

    private static int idCount = 0;

    public Database() {
        try {
            conn = connectToDatabase();
            if (conn != null) {
                System.out.println("Connected to database");

                //conn.prepareStatement("DROP TABLE IF EXISTS userTable;").execute();
                //conn.createStatement().execute("INSERT INTO userTable VALUES (1, 'database1', 'database1p');");

                // Check table is built
                try {
                    conn.prepareStatement("CREATE TABLE userTable (Id int NOT NULL UNIQUE PRIMARY KEY, Username VARCHAR(30) NOT NULL, Password VARCHAR(30) NOT NULL, Picture BLOB DEFAULT NULL);").execute();
                } catch (SQLException e) {
                    System.out.println("Table already exists. ");
                }

                ResultSet maxCount = conn.createStatement().executeQuery("SELECT MAX(id) as largestid FROM userTable");
                maxCount.next();
                idCount = maxCount.getInt("largestid") + 1;

                // For testing
                System.out.println(this);
                System.out.println(idCount);
                boolean yes = addUser("admin", "password");
                System.out.println(yes);
                System.out.println(this);
                System.out.println(idCount);

                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to database and handles errors.
     * @return Connection object used to make statements to
     */
    private Connection connectToDatabase() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database. ");
        }
        return conn;
    }

    /**
     * Returns the entire database saved.
     * @return String holding all items in database
     */
    public String toString() {
        //Get data back
        StringBuilder databaseString = new StringBuilder();
        try {
            conn = connectToDatabase();
            if (conn != null) {
                ResultSet allTable = conn.createStatement().executeQuery("SELECT * FROM userTable");
                //Iterate through ResultSet to get data
                while (allTable.next()) {
                    databaseString
                            .append("ID: ").append(allTable.getString("ID"))
                            .append("  Username: ").append(allTable.getString("Username"))
                            .append("  Password: ").append(allTable.getString("password"))
                            .append("\n");
                }
                conn.close();
            }

        } catch (SQLException ignored) {}

        return databaseString.toString();
    }



    /**
     * Checks if the user is in the database and has the right password.
     * @return boolean of if the user is in the database
     */
    public boolean inDatabase(String username, String password) {
        boolean isInDatabase = false;
        conn = connectToDatabase();
        if (conn != null) {
            try {
                String sqlStatement = "SELECT * FROM userTable WHERE username='" + username + "' AND password='" + password + "'";
                ResultSet resultingAccount = conn.createStatement().executeQuery(sqlStatement);
                if (resultingAccount.next()) {
                    isInDatabase = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return isInDatabase;
    }

    /**
     * Adds a user to the database with a new unique id number.
     * @param username Username for user
     * @param password Password for user
     * @return Boolean of if the item was added to the database correctly.
     */
    public boolean addUser(String username, String password) {
        boolean addedToDatabase = false;
        conn = connectToDatabase();
        if (conn != null) {
            try {
                String sqlStatement = "INSERT INTO userTable VALUES (" + idCount + ", '" + username + "', '" + password  + "', NULL);";
                conn.prepareStatement(sqlStatement).execute();
                idCount++;
                addedToDatabase = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return addedToDatabase;
    }

    /**
     * Adds an image to an existing user in the database.
     * @param image An image file
     * @return Boolean of if the image was added to the user in the database correctly.
     */
    public boolean addUserImage(int userId, File image) {
        boolean addedToDatabase = false;
        conn = connectToDatabase();
        if (conn != null) {
            try {
                String blob = createBlob(image);
                String sqlStatement = "UPDATE userTable SET Picture=" + blob + " WHERE ID=" + userId + ";";
                conn.prepareStatement(sqlStatement).execute();
                addedToDatabase = true;
            } catch (SQLException ignored) {}
        }
        return addedToDatabase;
    }

    private String createBlob(File file) {
        int finalBlob = 0;
        try {
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream in = new FileInputStream(file);
            finalBlob = in.read(fileData);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (finalBlob != 0) {
            return String.valueOf(finalBlob);
        } else {
            return "null";
        }

    }
}
