package nz.ac.canterbury.seng302.identityprovider;

import java.io.*;
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
                //conn.createStatement().execute("INSERT INTO userTable VALUES (1, 'admin', 'password', 'Administrator Account', 'test@gmail.com', NULL);");

                // Check table is built
                try {
                    conn.prepareStatement("CREATE TABLE userTable (" +
                            "Id int NOT NULL UNIQUE PRIMARY KEY, " +
                            "Username VARCHAR(30) NOT NULL, " +
                            "Password VARCHAR(50) NOT NULL, " +
                            "FullName VARCHAR(50) NOT NULL, " +
                            "Email VARCHAR(30) NOT NULL, " +
                            "Picture BLOB DEFAULT NULL" +
                            ");").execute();
                } catch (SQLException e) {
                    System.out.println("Table already exists. ");
                }

                ResultSet maxCount = conn.createStatement().executeQuery("SELECT MAX(id) as largestid FROM userTable");
                maxCount.next();
                idCount = maxCount.getInt("largestid") + 1;

                // For testing
                System.out.println(this);
                boolean yes = addUser("admin", "password", "Administrator Account", "test@gmail.com");
                System.out.println(yes);
                System.out.println(idCount);
                //System.out.println(this);


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
                            .append("  Password: ").append(allTable.getString("Password"))
                            .append("  Full Name: ").append(allTable.getBlob("FullName"))
                            .append("  Email: ").append(allTable.getString("Email"))
                            .append("  Picture: ").append(allTable.getBlob("Picture"))
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
            } catch (SQLException ignored) {}

        }
        return isInDatabase;
    }

    /**
     * Adds a user to the database with a new unique id number.
     * @param username Username for user
     * @param password Password for user
     * @param fullName The full name of the user
     * @param email Email for user
     * @return Boolean of if the item was added to the database correctly.
     */
    public boolean addUser(String username, String password, String fullName, String email) {
        boolean addedToDatabase = false;
        conn = connectToDatabase();
        if (conn != null) {
            try {
                String sqlStatement = "INSERT INTO userTable VALUES (" +
                        idCount + ", '" +
                        username + "', '" +
                        password  + "', '" +
                        fullName + "', '" +
                        email + "', " +
                        "NULL" +
                        ");";
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
                conn.close();
            } catch (SQLException ignored) {}
        }
        return addedToDatabase;
    }

    /**
     * Makes blob from file given (Probably not correctly)
     * @param file File to convert to Blob
     * @return Blob as string
     */
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

    public String getStringFromDatabase(int id, String column) {
        String result = null;
        conn = connectToDatabase();
        if (conn != null) {
            try {
                String sqlStatement = "SELECT " + column + " FROM userTable WHERE ID=" + id + ";";
                ResultSet accountReceived = conn.createStatement().executeQuery(sqlStatement);
                accountReceived.next();
                result = accountReceived.getString(column);
                conn.close();
            } catch (SQLException ignored) {}
        }
        return result;
    }

    public Integer getIntFromDatabase(int id, String column) {
        Integer result = null;
        conn = connectToDatabase();
        if (conn != null) {
            try {
                String sqlStatement = "SELECT " + column + " FROM userTable WHERE ID=" + id + ";";
                ResultSet accountReceived = conn.createStatement().executeQuery(sqlStatement);
                accountReceived.next();
                result = accountReceived.getInt(column);
                conn.close();
            } catch (SQLException ignored) {}
        }
        return result;
    }

    public Integer getIdFromDatabase(String username) {
        Integer result = null;
        conn = connectToDatabase();
        if (conn != null) {
            try {
                String sqlStatement = "SELECT id FROM userTable WHERE username='" + username + "';";
                ResultSet accountReceived = conn.createStatement().executeQuery(sqlStatement);
                accountReceived.next();
                result = accountReceived.getInt("id");
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
