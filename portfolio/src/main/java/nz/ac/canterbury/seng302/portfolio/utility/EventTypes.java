package nz.ac.canterbury.seng302.portfolio.utility;

import java.util.Objects;

/**
 * Object used to help with hashing the events to display on the calendar.
 * This is so that we can separate the types of events but still have the dates as the key.
 */
public class EventTypes {
    private String type;
    private String date;

    public EventTypes(String type, String date) {
        this.type = type;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, type);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof EventTypes eventTypes)) {
            return false;
        }
        return Objects.equals(date, eventTypes.date) && Objects.equals(type, eventTypes.type);
    }
}
