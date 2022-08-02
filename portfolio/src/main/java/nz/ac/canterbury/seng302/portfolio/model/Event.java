package nz.ac.canterbury.seng302.portfolio.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sun.istack.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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
    private String startDateColour;
    private String endDateColour;

    @Transient
    private final SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd/MMM/yyyy h:mm a");

    @Transient
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy h:mm a");


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

    /**
     * Get event start date month as a 3 letter string.
     * @return 3 letter string of the start month. Null if start date is null.
     */
    public String getEventStartMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
        if (eventStartDate != null) {
            return (dateFormat.format(eventStartDate));
        }
        return null;
    }
    /**
     * Get event start date as a detailed string.
     * @return String of the start date. Null if start date is null.
     */
    public String getStartDateDetail()  {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("h:mm a");
        if (eventStartDate != null) {
            return (Project.dateToString(eventStartDate)  + " " + eventStartTime.format(dateFormat));
        }
        return null;

    }

    /**
     * Sets the start date and time with a detailed string. Format example: 12/Jun/2022 12:53pm
     * @param startDateDetail start date to set
     * @throws ParseException when the given string isn't in the right format
     */
    public void setStartDateDetail(String startDateDetail) throws ParseException {
        eventStartDate = simpleDateFormatter.parse(startDateDetail);

        LocalDateTime date = LocalDateTime.parse(startDateDetail, dateFormatter);
        eventStartTime = LocalTime.of(date.getHour(), date.getMinute());
    }

    /**
     * Get event end date as a detailed string.
     * @return String of the end date. Null if end date is null.
     */
    public String getEndDateDetail()  {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("h:mm a");
        if (eventStartDate != null) {
            return (Project.dateToString(eventEndDate) + " " + eventEndTime.format(dateFormat));
        }
        return null;
    }

    /**
     * Sets the end date and time with a detailed string. Format example: 12/Jun/2022 12:53pm
     * @param endDateDetail start date to set
     * @throws ParseException when the given string isn't in the right format
     */
    public void setEndDateDetail(String endDateDetail) throws ParseException {
        eventEndDate = simpleDateFormatter.parse(endDateDetail);

        LocalDateTime date = LocalDateTime.parse(endDateDetail, dateFormatter);
        eventEndTime = LocalTime.of(date.getHour(), date.getMinute());
    }

    /**
     * Get day of the start date as an integer.
     * @return Integer of the start date day of the month. 0 if start date is null.
     */
    public int getEventStartDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        if (eventStartDate != null) {
            return Integer.parseInt(dateFormat.format(eventStartDate));
        }
        return 0;

    }

    public void setEventStartDate(Date eventStartDate) {
        if (this.eventStartTime != null) {
            this.eventStartDate = addTimeToDate(eventStartDate, this.eventStartTime);
        } else {
            this.eventStartDate = eventStartDate;
        }

    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        if (this.eventEndTime != null) {
            this.eventEndDate = addTimeToDate(eventEndDate, this.eventEndTime);
        } else {
            this.eventEndDate = eventEndDate;
        }

    }

    public LocalTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalTime eventStartTime) {
        this.eventStartTime = eventStartTime;
        if (this.eventStartDate != null) {
            this.eventStartDate = addTimeToDate(this.eventStartDate, eventStartTime);
        }
    }

    public LocalTime getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(LocalTime eventEndTime) {
        this.eventEndTime = eventEndTime;
        if (this.eventEndDate != null) {
            this.eventEndDate = addTimeToDate(this.eventEndDate, eventEndTime);
        }

    }

    /**
     * Takes a time and a date and adds the time to the date object and returns the changed date.
     * @param date Date that as no time.
     * @param time LocalTime time.
     * @return Date with time added to it.
     */
    private Date addTimeToDate(@NotNull Date date, @NotNull LocalTime time){
        SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
        try {
            Date dateWithTime = timeFormat.parse(String.valueOf(time));
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
            SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourFormat.format(dateWithTime)));
            cal.set(Calendar.MINUTE, Integer.parseInt(minuteFormat.format(dateWithTime)));
            return cal.getTime();
        } catch (ParseException e) {
            return date;
        }
    }


    public void setStartDateString(String date) {
        if (this.eventStartTime == null) {
            this.eventStartDate = Project.stringToDate(date);
        } else {
            this.eventStartDate = addTimeToDate(Project.stringToDate(date), this.eventStartTime);
        }
    }

    public String getStartDateString() {
        return Project.dateToString(this.eventStartDate);
    }

    public void setEndDateString(String date) {
        if (this.eventEndTime == null) {
            this.eventEndDate = Project.stringToDate(date);
        } else {
            this.eventEndDate = addTimeToDate(Project.stringToDate(date), this.eventEndTime);
        }
    }

    public String getEndDateString() {
        return Project.dateToString(this.eventEndDate);
    }

    public void setStartTimeString(String time) {
        this.eventStartTime = Event.stringToTime(time);
        if (this.eventStartDate != null) {
            this.eventStartDate = addTimeToDate(this.eventStartDate, this.eventStartTime);
        }
    }

    public String getStartTimeString() {
        return Event.timeToString(this.eventStartTime);
    }

    public void setEndTimeString(String time) {
        this.eventEndTime = Event.stringToTime(time);
        if (this.eventEndDate != null) {
            this.eventEndDate = addTimeToDate(this.eventEndDate, this.eventEndTime);
        }
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

    public String getStartDateColour() {
        return startDateColour;
    }

    public void setStartDateColour(String startDateColour) {
        this.startDateColour = startDateColour;
    }

    public String getEndDateColour() {
        return endDateColour;
    }

    public void setEndDateColour(String endDateColour) {
        this.endDateColour = endDateColour;
    }
}
