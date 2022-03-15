package nz.ac.canterbury.seng302.portfolio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;

import java.time.LocalDate;

import nz.ac.canterbury.seng302.portfolio.model.*;

/**
 * Provides static methods for communicating with the database.
 */
public class DatabaseConfig {

    /**
     * File path to the database.
     */
    private static final String DB_PATH = "jdbc:h2:file:./src/main/resources/data/projects";

    /**
     * Database username.
     */
    private static final String USER = "sa";

    /**
     * Database password.
     */
    private static final String PASS = "";

    /**
     * SQL statement for creating the project table.
     */
    private static final String PROJECT_SCHEMA = "create table if not exists project (\n" +
            "    name varchar(20) not null primary key,\n" +
            "    description varchar(250),\n" +
            "    startDate date not null,\n" +
            "    endDate date not null\n" +
            ");";

    /**
     * SQL statement for creating the sprint table.
     */
    private static final String SPRINT_SCHEMA = "create table if not exists sprint (\n" +
            "    labelNum int not null,\n" +
            "    name varchar(20) not null,\n" +
            "    description varchar(250),\n" +
            "    startDate date not null,\n" +
            "    endDate date not null,\n" +
            "    projectName varchar(20) not null references project,\n" +
            "    primary key (labelNum, projectName)\n" +
            ");";

    /**
     * Executes the given SQL statement and returns true if it was successful, otherwise false.
     * @param statement SQL statement to be executed
     * @return true if statement was successfully executed, otherwise false
     */
    private static boolean executeStatement(String statement) {
        Connection conn = null;
        Statement stmt = null;
        boolean success = false;
        try {
            conn = DriverManager.getConnection(DB_PATH, USER, PASS);
            stmt = conn.createStatement();
            stmt.execute(statement);
            success = true;
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt!=null) stmt.close();
            } catch (SQLException se2) {}
            try {
                if (conn!=null) conn.close();
            } catch (SQLException se) {}
        }
        return success;
    }

    /**
     * Creates a project and sprint table in the database.
     */
    public static void createTables() {
        executeStatement(PROJECT_SCHEMA);
        executeStatement(SPRINT_SCHEMA);
    }


    /**
     * Inserts the given project into the database and returns true if successful, otherwise false.
     * @param project project to insert
     * @return true if successful, otherwise false
     */
    public static boolean insertProject(Project project) {
        String stmt = "insert into project values " + "('" + project.getName() + "', '" + project.getDescription() +
                "', '" + project.getStartDate() + "', '" + project.getEndDate() + "')";
        boolean success = executeStatement(stmt);
        return success;
    }

    /**
     * Inserts the given sprint into the database and returns true if successful, otherwise false.
     * @param sprint sprint to insert
     * @return true if successful, otherwise false
     */
    public static boolean insertSprint(Sprint sprint) {
        String stmt = "insert into sprint values " + "('" + sprint.getLabelNum() + "', '" + sprint.getName() + "', '" +
                sprint.getDescription() + "', '" + sprint.getStartDate() + "', '" + sprint.getEndDate() + "', '" +
                sprint.getProjectName() + "')";
        boolean success = executeStatement(stmt);
        return success;
    }

    /**
     * Returns the projects given when executing the given SQL statement.
     * E.g. "select * from project" will return all projects in the database.
     * @param statement SQL statement to be executed
     * @return resulting projects from SQL statement
     */
    public static ArrayList<Project> getProjects(String statement) {
        Connection conn = null;
        Statement stmt = null;
        ArrayList<Project> projects = new ArrayList<Project>();
        try {
            conn = DriverManager.getConnection(DB_PATH, USER, PASS);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(statement);
            while (rs.next()) {
                Project project = new Project(rs.getString("name"), rs.getString("description"),
                        LocalDate.parse(rs.getString("startDate")), LocalDate.parse(rs.getString("endDate")));
                projects.add(project);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt!=null) stmt.close();
            } catch (SQLException se2) {}
            try {
                if (conn!=null) conn.close();
            } catch (SQLException se) {}
        }
        ArrayList<Project> finalProject = new ArrayList<Project>();
        finalProject.add(projects.get(projects.size() - 1));
        return finalProject;
    }

    /**
     * Returns the sprints given when executing the given SQL statement.
     * E.g. "select * from sprint" will return all sprints in the database.
     * @param statement SQL statement to be executed
     * @return resulting sprints from SQL statement
     */
    public static ArrayList<Sprint> getSprints(String statement) {
        Connection conn = null;
        Statement stmt = null;
        ArrayList<Sprint> sprints = new ArrayList<Sprint>();
        try {
            conn = DriverManager.getConnection(DB_PATH, USER, PASS);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(statement);
            while (rs.next()) {
                Sprint sprint = new Sprint(rs.getInt("labelNum"), rs.getString("name"), rs.getString("description"),
                        LocalDate.parse(rs.getString("startDate")), LocalDate.parse(rs.getString("endDate")),
                        rs.getString("projectName"));
                sprints.add(sprint);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt!=null) stmt.close();
            } catch (SQLException se2) {}
            try {
                if (conn!=null) conn.close();
            } catch (SQLException se) {}
        }
        return sprints;
    }

    /**
     * Gets the maximum sprint value currently within the database.
     *
     * @return  Largest sprint number.
     */
    public static int getMaxSprint() {
        int value = -1;  // If no value is found an impossible value of -1 it returned.
        // Using resources which automatically close the connection.
        try (Connection conn = DriverManager.getConnection(DB_PATH, USER, PASS); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT MAX(labelNum) as labelNum FROM sprint;");
            while (rs.next()) {
                value = rs.getInt("labelNum");
            }
            rs.close();
        } catch (Exception se) {
            se.printStackTrace();
        }
        return value;
    }

    /**
     * Update the sprint that the user selected
     *
     * @param sprint the sprint object that the user selected to be edited
     */
    public static boolean updateSprint(Sprint sprint){
        String stmt = "update sprint set startDate ='"+sprint.getStartDate()+"', endDate = '"+sprint.getEndDate()+"', description = '"+sprint.getDescription()+"' where labelNum ='"+sprint.getLabelNum()+"' and projectName ='"+sprint.getProjectName()+"';";
        boolean success = executeStatement(stmt);
        return success;
    }
}
