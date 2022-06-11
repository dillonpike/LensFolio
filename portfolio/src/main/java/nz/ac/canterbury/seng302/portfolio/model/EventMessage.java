package nz.ac.canterbury.seng302.portfolio.model;


/**
 * Message class that is sent when STOMP sending event updates
 */
public class EventMessage {

    private String eventName;

    private int eventId;

    private int userId;

    private String userFirstName;

    private String userLastName;

    private String username;

    /**
     * Main constructer of an EventMessage
     * @param eventName Name of the event being changed.
     * @param userId User Id of the user editing.
     * @param userFirstName First name of the user editing.
     * @param userLastName Last name of the user editing.
     * @param username Username of the user editing.
     */
    public EventMessage(String eventName, int eventId, int userId, String userFirstName, String userLastName, String username) {
        this.eventName = eventName;
        this.eventId = eventId;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.username = username;
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

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
