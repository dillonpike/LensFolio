package nz.ac.canterbury.seng302.portfolio.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * JPA entity that models a deadline of a project. Each deadline has a parent project that it's in,
 * a name, a date (that includes a time).
 */
@Entity
public class Deadline {


    /**
     * Id of the deadline.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Id of the project the deadline is apart of.
     */
    private int parentProjectId;

    /**
     * Name of the deadline.
     */
    private String deadlineName;

    /**
     * Date of the deadline (includes time).
     */
    private Date deadlineDate;

    /**
     * Gets the id of the deadline
     * @return the id of the deadline
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the deadline
     * @param id id of the deadline
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the project id that the deadline is in
     * @return project id that the deadline is in
     */
    public int getParentProjectId() {
        return parentProjectId;
    }

    /**
     * Sets the project id that the deadline is in
     * @param parentProjectId project id that the deadline is in
     */
    public void setParentProjectId(int parentProjectId) {
        this.parentProjectId = parentProjectId;
    }

    /**
     * Gets the name of the deadline
     * @return name of the deadline
     */
    public String getDeadlineName() {
        return deadlineName;
    }

    /**
     * Sets the name of the deadline
     * @param deadlineName name of the deadline
     */
    public void setDeadlineName(String deadlineName) {
        this.deadlineName = deadlineName;
    }

    /**
     * Gets the date of the deadline
     * @return date of the deadline
     */
    public Date getDeadlineDate() {
        return deadlineDate;
    }

    /**
     * Sets the date of the deadline
     * @param deadlineDate date of the deadline
     */
    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    /**
     * Gets the day of the month of the deadline
     * @return day of the month of the deadline
     */
    public int getDeadlineDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return Integer.parseInt(dateFormat.format(deadlineDate));
    }

    /**
     * Gets the month of the year of the deadline
     * @return month of the year of the deadline
     */
    public String getDeadlineMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
        return (dateFormat.format(deadlineDate));
    }

    /**
     * Returns a string representation of the time of the deadline
     * @return string representation of the time of the deadline
     */
    public String getDeadlineTime()  {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        return (dateFormat.format(deadlineDate));
    }

    /**
     * Sets the time of the date stored in deadlineDate.
     * @param time Time in string format, formatted as: "h:mm a" (e.g. 12:37 am).
     * @throws ParseException Thrown if time parameter is given in the wrong format.
     */
    public void setDeadlineTime(String time) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        long milliseconds = timeFormat.parse(time).getTime();
        deadlineDate.setTime(milliseconds);
    }

    /**
     * Gets the deadline date in the format made by the Project class
     * ONLY GETS DATE, NOT TIME
     * @return Deadline date as a string.
     */
    public String getDeadlineDateString() {
        return Project.dateToString(this.deadlineDate);
    }

    /**
     * Empty constructor for JPA.
     */
    public Deadline() {}

    /**
     * Constructs a Deadline object.
     * @param parentProjectId the parent project of the deadline
     * @param deadlineName name of the deadline
     * @param deadlineDate date of the deadline
     */
    public Deadline(int parentProjectId, String deadlineName, Date deadlineDate) {
        this.parentProjectId = parentProjectId;
        this.deadlineName = deadlineName;
        this.deadlineDate = deadlineDate;
    }

    /**
     * String representation of the deadline object
     * @return String representation of the deadline object
     */
    @Override
    public String toString() {
        return String.format(
                "deadline[id=%d, parentProjectId='%d', deadlineName='%s', deadlineDate='%s']",
                id, parentProjectId, deadlineName, deadlineDate);
    }

    /**
     * Returns the deadline as a JSON string.
     * @return deadline as a JSON string
     * @throws JsonProcessingException when the deadline cannot be converted to a JSON string
     */
    public String toJSONString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
