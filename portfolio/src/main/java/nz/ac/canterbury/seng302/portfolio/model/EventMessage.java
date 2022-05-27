package nz.ac.canterbury.seng302.portfolio.model;

/**
 * Message class that is sent when STOMP sending event updates
 */
public class EventMessage {

    private String eventName;

    private int eventId;

    /**
     * Main constructer of an EventMessage
     * @param eventId Id of the event.
     * @param eventName Event name for displaying.
     */
    public EventMessage(int eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String sprintName) {
        this.eventName = sprintName;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
