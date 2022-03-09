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

                updateMaxId();

                // For testing
                boolean yes = addUser("admin", "password", "Administrator Account", "test@gmail.com");

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

    private void updateMaxId() throws SQLException {
        ResultSet maxCount = conn.createStatement().executeQuery("SELECT MAX(id) as largestid FROM userTable");
        maxCount.next();
        idCount = maxCount.getInt("largestid") + 1;
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
                updateMaxId();
                String sqlStatement = "INSERT INTO userTable (id, username, password, fullname, email) VALUES (" +
                        idCount + ", '" +
                        username + "', '" +
                        password  + "', '" +
                        fullName + "', '" +
                        email + "'" +
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

    /**
     * Gets string variables from the database based on the given column.
     * @param id Id of user that the data is coming from
     * @param column Data description for column that holds strings
     * @return Received data
     */
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

    /**
     * Gets integer variables from the database based on the given column.
     * @param id Id of user that the data is coming from
     * @param column Data description for column that holds integers
     * @return Received data
     */
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

    /**
     * Gets the ID of the given username.
     * @param username Username of wanted user
     * @return ID of user
     */
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
            } catch (SQLException ignored) {}
        }
        return result;
    }

    /**
     * Updates a column in the database based on which column and id is given.
     * @param id ID of the user that you want to change the data of
     * @param column keyword of column you want to update
     * @param data New data to be updated to
     * @return Whether the data was updated successfully
     */
    public boolean setStringFromDatabase(int id, String column, String data) {
        boolean wasUpdated = false;
        conn = connectToDatabase();
        if (conn != null) {
            try {
                String sqlStatement = "UPDATE userTable SET " + column + "='" + data + "' WHERE id=" + id + ";";
                ResultSet accountReceived = conn.createStatement().executeQuery(sqlStatement);
                accountReceived.next();
                wasUpdated = true;
                conn.close();
            } catch (SQLException ignored) {}
        }
        return wasUpdated;
    }

    /**
     * Updates a column in the database based on which column and id is given.
     * @param id ID of the user that you want to change the data of
     * @param column keyword of column you want to update
     * @param data New data to be updated to
     * @return Whether the data was updated successfully
     */
    public boolean setIntFromDatabase(int id, String column, int data) {
        boolean wasUpdated = false;
        conn = connectToDatabase();
        if (conn != null) {
            try {
                String sqlStatement = "UPDATE userTable SET " + column + "=" + data + " WHERE id=" + id + ";";
                ResultSet accountReceived = conn.createStatement().executeQuery(sqlStatement);
                accountReceived.next();
                wasUpdated = true;
                conn.close();
            } catch (SQLException ignored) {}
        }
        return wasUpdated;
    }
}
