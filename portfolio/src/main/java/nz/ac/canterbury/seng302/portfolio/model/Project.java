package nz.ac.canterbury.seng302.portfolio.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.format.annotation.DateTimeFormat;

@Entity // this is an entity, assumed to be in a table called Project
public class Project {
    @Id
    private int id;
    private String projectName;
    private String projectDescription;
    private Date projectStartDate;
    private Date projectEndDate;


    protected Project() {}

    public Project(String projectName, String projectDescription, Date projectStartDate, Date projectEndDate) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartDate = projectStartDate;
        this.projectEndDate = projectEndDate;
    }

    public Project(String projectName, String projectDescription, String projectStartDate, String projectEndDate) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartDate = Project.stringToDate(projectStartDate);
        this.projectEndDate = Project.stringToDate(projectEndDate);
    }

    @Override
    public String toString() {
        return String.format(
                "Project[id=%d, projectName='%s', projectStartDate='%s', projectEndDate='%s', projectDescription='%s']",
                id, projectName, projectStartDate, projectEndDate, projectDescription);
    }

    /**
     * Gets the date form of the given date string
     *
     * @param dateString the string to read as a date in format 01/Jan/2000
     * @return the given date, as a date object
     */
    public static Date stringToDate(String dateString) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MMM/yyyy").parse(dateString);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        return date;
    }

    /**
     * Gets the string form of the given date in
     *
     * @param date the date to convert
     * @return the given date, as a string in format 01/Jan/2000
     */
    public static String dateToString(Date date) {
        // Returns date in format unless its null which it then makes a new Date object.
        return new SimpleDateFormat("dd/MMM/yyyy").format(Objects.requireNonNullElseGet(date, Date::new));
    }

    /* Getters/Setters */

    public int getId() {
        return  id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return projectName;
    }

    public void setName(String newName) {
        this.projectName = newName;
    }

    public String getDescription() {
        return projectDescription;
    }

    public void setDescription(String newDescription) {
        this.projectDescription = newDescription;
    }

    /* Dates have string get/set methods to interact with view */

    public Date getStartDate() {
        return projectStartDate;
    }

    public String getStartDateString() {
        return Project.dateToString(this.projectStartDate);
    }

    public void setStartDate(Date newStartDate) {
        this.projectStartDate = newStartDate;
    }

    public void setStartDateString(String date) {
        this.projectStartDate = Project.stringToDate(date);
    }

    public Date getEndDate() {
        return projectEndDate;
    }

    public String getEndDateString() {
        return Project.dateToString(this.projectEndDate);
    }

    public void setEndDate(Date newEndDate) {
        this.projectEndDate = newEndDate;
    }

    public void setEndDateString(String date) {
        this.projectEndDate = Project.stringToDate(date);
    }

    public String toJSONString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
