package nz.ac.canterbury.seng302.identityprovider;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Database {

    private Connection conn;

    public Database() {
        try {
            conn = connectToDatabase();
            if (conn != null) {
                System.out.println("Connected to database");

                //conn.prepareStatement("DROP TABLE IF EXISTS userTable;").execute();
                //conn.createStatement().execute("INSERT INTO userTable VALUES (1, 'database1', 'database1p');");

                // Check table is built
                try {
                    conn.prepareStatement("CREATE TABLE userTable (Id int NOT NULL UNIQUE PRIMARY KEY, Username VARCHAR(30) NOT NULL, Password VARCHAR(30) NOT NULL);").execute();
                } catch (SQLException ignored) {}

                // For testing
                System.out.println(this);

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
}
