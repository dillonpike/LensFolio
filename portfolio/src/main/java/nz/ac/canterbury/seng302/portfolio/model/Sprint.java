package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Models a sprint, which contains a label, name, description, start date, and end date.
 */
public class Sprint implements Serializable {

    /**
     * Error message for when the start date is invalid due to not being before the end date.
     */
    private static final String START_DATE_ERROR_MSG = "Start date must be before the end date.";

    /**
     * Error message for when the end date is invalid due to not being after the start date.
     */
    private static final String END_DATE_ERROR_MSG = "End date must be after the start date.";


    /**
     * Label number for the sprint.
     * Used in the sprint label which contains the word 'Sprint' followed by a number, e.g. 'Sprint 1'.
     */
    private int labelNum;

    /**
     * Name of the sprint.
     */
    private String name;

    /**
     * Description of the sprint.
     */
    private String description;

    /**
     * Start date of the sprint.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * End date of the sprint. Must be after startDate.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * Name of the project the sprint is apart of.
     */
    private String projectName;

    /**
     * Constructor to make a new sprint.
     * @param labelNum number to be used in the sprint label
     * @param name name of the sprint
     * @param description description of the sprint
     * @param startDate start date of the sprint
     * @param endDate end date of the sprint
     * @param projectName name of the project the sprint is apart of
     */
    public Sprint(int labelNum, String name, String description, LocalDate startDate, LocalDate endDate,
                  String projectName) throws Exception {
        if (startDate.compareTo(endDate) >= 0) {
            throw new Exception(END_DATE_ERROR_MSG);
        }
        this.labelNum = labelNum;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectName = projectName;
    }

    /**
     * Constructor for an empty sprint.
     */
    public Sprint(){};

    /**
     * Gets the sprint label.
     * @return sprint label
     */
    public String getLabel() {
        return "Sprint " + labelNum;
    }


    /**
     * Gets the number of the sprint label.
     * @return number of the sprint label
     */
    public int getLabelNum() {
        return labelNum;
    }

    /**
     * Sets a new number for the sprint label.
     * @param labelNum new number for the sprint label
     */
    public void setLabelNum(int labelNum) {
        this.labelNum = labelNum;
    }

    /**
     * Gets the name of the sprint.
     * @return name of the sprint
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the sprint.
     * @param name name of the sprint
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the sprint.
     * @return description of the sprint
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the sprint
     * @param description description of the sprint
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the start date of the sprint.
     * @return start date of the sprint
     */
    public LocalDate getStartDate() {
        if (startDate == null) {
            return null;
        } else {
            return startDate;
        }
    }

    /**
     * Sets the start date of the sprint.
     * @param startDate start date of the sprint
     */
    public void setStartDate(LocalDate startDate) throws Exception {
        if (endDate != null) {
            if (startDate.compareTo(endDate) >= 0) {
                throw new Exception(START_DATE_ERROR_MSG);
            }
        }
        this.startDate = startDate;
    }

    /**
     * Sets the end date of the sprint.
     * @return end date of the sprint
     */
    public LocalDate getEndDate() {
        if (endDate == null) {
            return null;
        } else {
            return endDate;
        }
    }

    /**
     * Gets the end date of the sprint.
     * @param endDate end date of the sprint
     */
    public void setEndDate(LocalDate endDate) throws Exception {
        if (startDate != null) {
            if (startDate.compareTo(endDate) >= 0) {
                throw new Exception(END_DATE_ERROR_MSG);
            }
        }
        this.endDate = endDate;
    }

    /**
     * Gets the name of the project the sprint is apart of.
     * @return name of the project the sprint is apart of
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the name of the project the sprint is apart of.
     * @param projectName name of the project the sprint is apart of
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}