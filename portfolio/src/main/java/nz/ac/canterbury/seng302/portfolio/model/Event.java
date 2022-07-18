package nz.ac.canterbury.seng302.portfolio.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.format.DateTimeFormatter;
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

    public Event(int parentProjectId, String eventName, Date eventStartDate, Date eventEndDate, LocalTime startTime, LocalTime endTime) {
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

    /**
     * Gets the time form of the given time string
     *
     * @param timeString the string to read as a date in format 11:45:30
     * @return the given time, as a time object
     */
    public static LocalTime stringToTime(String timeString) {
        LocalTime time = null;
        try {
            time = LocalTime.parse(timeString);
        } catch (Exception e) {
            System.err.println("Error parsing time: " + e.getMessage());
        }
        return time;
    }

    /**
     * Gets the string form of the given time
     * @param time the date to convert
     * @return the given date, as a string in format 11:45:30
     */
    public static String timeToString(LocalTime time) {
        // Returns time in format unless its null which it then makes a new Date object.
        String newTime = "";
        if (time != null) {
            try {
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss");
                newTime = time.format(myFormatObj);
            } catch (Exception e) {
                System.err.println("Error parsing time to string: " + e.getMessage());
            }
        }
        return newTime;
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

    public String getEventStartMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
        return (dateFormat.format(eventStartDate));
    }

    public String getStartDateDetail()  {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("h:mm a");
        return (Project.dateToString(eventStartDate)  + " " + eventStartTime.format(dateFormat));
    }

    public String getEndDateDetail()  {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("h:mm a");
        return (Project.dateToString(eventEndDate) + " " + eventEndTime.format(dateFormat));
    }

    public int getEventStartDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return Integer.parseInt(dateFormat.format(eventStartDate));
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
        this.eventStartTime = Event.stringToTime(time);
    }

    public String getStartTimeString() {
        return Event.timeToString(this.eventStartTime);
    }

    public void setEndTimeString(String time) {
        this.eventEndTime = Event.stringToTime(time);
    }

    public String getEndTimeString() {
        return Event.timeToString(this.eventEndTime);
    }

    /**
     * Returns the deadline as a JSON string.
     * @return deadline as a JSON string
     * @throws JsonProcessingException when the deadline cannot be converted to a JSON string
     */
    public String toJSONString() throws JsonProcessingException {
        ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
        return mapper.writeValueAsString(this);

    }
}
