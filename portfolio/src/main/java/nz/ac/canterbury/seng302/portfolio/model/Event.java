package nz.ac.canterbury.seng302.portfolio.model;

import java.time.LocalTime;
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
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    public Event() {}

    public Event(int id, int parentProjectId, String eventName, Date eventStartDate, Date eventEndDate, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.parentProjectId = parentProjectId;
        this.eventName = eventName;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventStartTime = startTime;
        this.eventEndTime = endTime;
    }

    @Override
    public String toString() {
        return String.format(
                "event[id=%d, parentProjectId='%d', eventName='%s', eventStartDate='%s', eventEndDate='%s', eventStartTime='%s', eventEndTime='%s']",
                id, parentProjectId, eventName, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
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

    public LocalTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public LocalTime getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(LocalTime eventEndTime) {
        this.eventEndTime = eventEndTime;
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

    public void setStartTimeString(String time) {
        this.eventStartTime = Project.stringToTime(time);
    }

    public String getStartTimeString() {
        return Project.timeToString(this.eventStartTime);
    }

    public void setEndTimeString(String time) {
        this.eventEndTime = Project.stringToTime(time);
    }

    public String getEndTimeString() {
        return Project.timeToString(this.eventEndTime);
    }

}
