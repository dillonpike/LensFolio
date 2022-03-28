package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

@Entity // this is an entity, assumed to be in a table called Sprint
public class Sprint {
    @Id
    private int id;
    private int parentProjectId;
    private String sprintName;
    private String sprintLabel;
    private String sprintDescription;
    private Date sprintStartDate;
    private Date sprintEndDate;

    public Sprint() {}

    public Sprint(int parentProjectId, String sprintName, String sprintLabel, String sprintDescription, Date sprintStartDate, Date sprintEndDate) {
        this.parentProjectId = parentProjectId;
        this.sprintName = sprintName;
        this.sprintLabel = sprintLabel;
        this.sprintDescription = sprintDescription;
        this.sprintStartDate = sprintStartDate;
        this.sprintEndDate = sprintEndDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Sprint[id=%d, parentProjectId='%d', sprintName='%s', sprintLabel='%s', sprintStartDate='%s', sprintEndDate='%s', sprintDescription='%s']",
                id, parentProjectId, sprintName, sprintLabel, sprintStartDate, sprintEndDate, sprintDescription);
    }

    public void setId(int id) { this.id = id; }
    public int getId(){
        return id;
    }
    public int getParentProjectId() {
        return parentProjectId;
    }
    public String getName() {
        return sprintName;
    }
    public String getLabel() {
        return sprintLabel;
    }
    public String getDescription(){
        return sprintDescription;
    }

    public String getDates() {
        return (getStartDateString() + " - " + getEndDateString());
    }

    public Date getStartDate() {
        return sprintStartDate;
    }

    public String getStartDateString() {
        return Project.dateToString(this.sprintStartDate);
    }

    public void setStartDate(Date newStartDate) {
        this.sprintStartDate = newStartDate;
    }

    public void setStartDateString(String date) {
        this.sprintStartDate = Project.stringToDate(date);
    }

    public Date getEndDate() {
        return sprintEndDate;
    }

    public String getEndDateString() {
        return Project.dateToString(this.sprintEndDate);
    }

    public void setEndDate(Date newEndDate) {
        this.sprintEndDate = newEndDate;
    }

    public void setEndDateString(String date) {
        this.sprintEndDate = Project.stringToDate(date);
    }

    public void setDescription(String sprintDescription) {
        this.sprintDescription = sprintDescription;
    }

    public void setLabel(String sprintLabel) {
        this.sprintLabel = sprintLabel;
    }

    public void setName(String sprintName) {
        this.sprintName = sprintName;
    }
}
