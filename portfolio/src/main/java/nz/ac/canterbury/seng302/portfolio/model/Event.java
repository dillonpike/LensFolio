package nz.ac.canterbury.seng302.portfolio.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import nz.ac.canterbury.seng302.portfolio.utility.DateUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private String startDateColour;
    private String endDateColour;

    @Transient
    private final SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd/MMM/yyyy h:mm a");

    @Transient
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy h:mm a");

    @Transient
    private static final Logger logger = LoggerFactory.getLogger(Event.class);


    public Event() {}

    public Event(int id, int parentProjectId, String eventName, Date eventStartDate, Date eventEndDate) {
        this.id = id;
        this.parentProjectId = parentProjectId;
        this.eventName = eventName;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public Event(int parentProjectId, String eventName, Date eventStartDate, Date eventEndDate) {
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
            logger.error(String.format("Error parsing time: %s", e.getMessage()));
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
                logger.error(String.format("Error parsing time to string: %s", e.getMessage()));
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
        if (eventStartDate != null) {
            return (Project.dateToString(eventStartDate)  + " " + getEventStartTime());
        }
        return null;

    }

    /**
     * Sets the start date and time with a detailed string. Format example: 12/Jun/2022 12:53pm
     * @param startDateDetail start date to set
     * @throws ParseException when the given string isn't in the right format
     */
    public void setStartDateDetail(String startDateDetail) throws ParseException {
        Date dateTime = DateUtility.stringToDateTime(startDateDetail);
        if (dateTime != null) {
            this.eventStartDate = dateTime;
        } else {
            if (this.eventStartDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(Project.stringToDate(startDateDetail));
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
                SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
                cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(hourFormat.format(eventStartDate)));
                cal.add(Calendar.MINUTE, Integer.parseInt(minuteFormat.format(eventStartDate)));
                this.eventStartDate = cal.getTime();
            } else {
                this.eventStartDate = Project.stringToDate(startDateDetail);
            }
        }
    }

    /**
     * Get event end date as a detailed string.
     * @return String of the end date. Null if end date is null.
     */
    public String getEndDateDetail()  {
        if (eventStartDate != null) {
            return (Project.dateToString(eventEndDate) + " " + getEventEndTime());
        }
        return null;
    }

    /**
     * Sets the end date and time with a detailed string. Format example: 12/Jun/2022 12:53pm
     * @param endDateDetail start date to set
     * @throws ParseException when the given string isn't in the right format
     */
    public void setEndDateDetail(String endDateDetail) throws ParseException {
        Date dateTime = DateUtility.stringToDateTime(endDateDetail);
        if (dateTime != null) {
            this.eventEndDate = dateTime;
        } else {
            if (this.eventEndDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(Project.stringToDate(endDateDetail));
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
                SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
                cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(hourFormat.format(eventEndDate)));
                cal.add(Calendar.MINUTE, Integer.parseInt(minuteFormat.format(eventEndDate)));
                this.eventEndDate = cal.getTime();
            } else {
                this.eventEndDate = Project.stringToDate(endDateDetail);
            }
        }
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
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventStartTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        if (eventStartDate != null) {
            return (dateFormat.format(eventStartDate));
        }
        return null;
    }

    public String getEventEndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        if (eventEndDate != null) {
            return (dateFormat.format(eventEndDate));
        }
        return null;
    }

    /**
     * Takes a time and a date and adds the time to the date object and returns the changed date.
     * This function is called from the javascript code
     * @param date Date that as no time.
     * @param time LocalTime time.
     * @return Date with time added to it.
     */
    public Date addTimeToDate(Date date,LocalTime time){
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

    public String getStartDateString() {
        return Project.dateToString(this.eventStartDate);
    }

    public String getEndDateString() {
        return Project.dateToString(this.eventEndDate);
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
