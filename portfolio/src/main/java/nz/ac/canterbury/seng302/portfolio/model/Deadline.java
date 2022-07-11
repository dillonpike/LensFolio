package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
}
