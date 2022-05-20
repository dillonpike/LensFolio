package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private int parentProjectId;
    private String eventName;
    private Date eventStartDate;
    private Date eventEndDate;

    public Event() {}

    public Event(int id, int parentProjectId, String eventName, Date eventStartDate, Date eventEndDate) {
        this.id = id;
        this.parentProjectId = parentProjectId;
        this.eventName = eventName;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    @Override
    public String toString() {
        return String.format(
                "event[id=%d, parentProjectId='%d', eventName='%s', eventStartDate='%s', eventEndDate='%s']",
                id, parentProjectId, eventName, eventStartDate, eventEndDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentProjectId() {
        return parentProjectId;
    }

    public void setParentProjectId(int parentProjectId) {
        this.parentProjectId = parentProjectId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }



    public void setStartDateString(String date) {
        this.eventStartDate = Project.stringToDate(date);
    }

    public String getStartDateString() {
        return Project.dateToString(this.eventStartDate);
    }

    public void setEndDateString(String date) {
        this.eventEndDate = Project.stringToDate(date);
    }

    public String getEndDateString() {
        return Project.dateToString(this.eventEndDate);
    }

}
