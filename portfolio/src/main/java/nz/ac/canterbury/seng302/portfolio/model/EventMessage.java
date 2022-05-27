package nz.ac.canterbury.seng302.portfolio.model;

/**
 * Message class that is sent when STOMP sending event updates
 */
public class EventMessage {

    private String eventName;

    private int userId;

    /**
     * Main constructer of an EventMessage
     * @param userId Id of the user editing the event.
     * @param eventName Event name for displaying.
     */
    public EventMessage(int userId, String eventName) {
        this.userId = userId;
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String sprintName) {
        this.eventName = sprintName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
