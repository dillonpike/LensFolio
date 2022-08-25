package nz.ac.canterbury.seng302.portfolio.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * JPA entity that models a milestone of a project. Each deadline has a parent project that it's in,
 * a name, a date (that includes a time).
 */
@Entity
public class Milestone {
    /**
     * Id of the milestone.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Id of the project the milestone is apart of.
     */
    private int parentProjectId;

    /**
     * Name of the milestone.
     */
    private String milestoneName;

    /**
     * Date of the milestone (includes time).
     */
    private Date milestoneDate;



    private String colour;

    /**
     * Gets the id of the milestone
     * @return the id of the milestone
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the milestone
     * @param id id of the milestone
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the project id that the milestone is in
     * @return project id that the milestone is in
     */
    public int getParentProjectId() {
        return parentProjectId;
    }

    /**
     * Sets the project id that the milestone is in
     * @param parentProjectId project id that the milestone is in
     */
    public void setParentProjectId(int parentProjectId) {
        this.parentProjectId = parentProjectId;
    }

    /**
     * Gets the name of the milestone
     * @return name of the milestone
     */
    public String getMilestoneName() {
        return milestoneName;
    }

    /**
     * Sets the name of the milestone
     * @param milestoneName name of the milestone
     */
    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    /**
     * Gets the date of the milestone
     * @return date of the milestone
     */
    public Date getMilestoneDate() {
        return milestoneDate;
    }

    /**
     * Sets the date of the milestone
     * @param milestoneDate date of the milestone
     */
    public void setMilestoneDate(Date milestoneDate) {
        this.milestoneDate = milestoneDate;
    }

    /**
     * Gets the day of the month of the milestone
     * @return day of the month of the milestone. 0 if milestone date is null
     */
    public int getMilestoneDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        if (milestoneDate != null) {
            return Integer.parseInt(dateFormat.format(milestoneDate));
        }
        return 0;
    }

    /**
     * Gets the month of the year of the milestone
     * @return month of the year of the milestone. Null if milestone date is null
     */
    public String getMilestoneMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
        if (milestoneDate != null) {
            return (dateFormat.format(milestoneDate));
        }
        return null;
    }

    /**
     * Empty constructor for JPA.
     */
    public Milestone() {}

    /**
     * Constructs a Milestone object.
     * @param parentProjectId the parent project of the milestone
     * @param milestoneName name of the milestone
     * @param milestoneDate date of the milestone
     */
    public Milestone(int parentProjectId, String milestoneName, Date milestoneDate) {
        this.parentProjectId = parentProjectId;
        this.milestoneName = milestoneName;
        this.milestoneDate = milestoneDate;
    }

    /**
     * String representation of the milestone object
     * @return String representation of the milestone object
     */
    @Override
    public String toString() {
        return String.format(
                "milestone[id=%d, parentProjectId='%d', milestoneName='%s', milestoneDate='%s']",
                id, parentProjectId, milestoneName, milestoneDate);
    }

    /**
     * Sets the milestone date with a string.
     * @param date new date
     */
    public void setMilestoneDateString(String date) {
        this.milestoneDate = Project.stringToDate(date);
    }

    /**
     * Returns the milestone date as a string.
     * @return milestone date as a string
     */
    public String getMilestoneDateString() {
        return Project.dateToString(this.milestoneDate);
    }

    /**
     * Returns the milestone as a JSON string.
     * @return milestone as a JSON string
     * @throws JsonProcessingException when the milestone cannot be converted to a JSON string
     */
    public String toJSONString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
