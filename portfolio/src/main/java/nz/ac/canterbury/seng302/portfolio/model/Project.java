package nz.ac.canterbury.seng302.portfolio.model;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * Models a project, which contains a name, description, start date, and end date.
 */
public class Project implements Serializable {

    /**
     * Error message for when the start date is invalid due to not being before the end date.
     */
    private static final String START_DATE_ERROR_MSG = "Start date must be before the end date.";

    /**
     * Error message for when the end date is invalid due to not being after the start date.
     */
    private static final String END_DATE_ERROR_MSG = "End date must be after the start date.";

    /**
     * Name of the project.
     */
    private String name;

    /**
     * Description of the project.
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
     * Constructor to make a new project.
     * @param name name of the project
     * @param description description of the project
     * @param startDate start date of the project
     * @param endDate end date of the project. Must be after startDate
     */
    public Project(String name, String description, LocalDate startDate, LocalDate endDate) throws Exception {
        if (startDate.compareTo(endDate) >= 0) {
            throw new Exception(END_DATE_ERROR_MSG);
        }
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Project(){};

    /**
     * Gets the name of the project.
     * @return name of the project
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the project.
     * @param name name of the project
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the project.
     * @return description of the project
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the project.
     * @param description description of the project
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


}