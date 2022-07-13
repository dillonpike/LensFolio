package nz.ac.canterbury.seng302.portfolio.model;

/**
 * The response message class sent that are received by STOMP subscriptions when events are edited.
 */
public class EventResponse {

    private String eventName;

    private int eventId;

    private String username;

    private String userFirstName;

    private String userLastName;

    private long dateOfCreation;

    /**
     * Main constructer for EventResponse
     * @param eventName Event name for displaying.
     * @param username Username of user editing.
     * @param userFirstName First name of user editing.
     * @param userLastName Last name of user editing.
     * @param dateOfCreation Date/Time in seconds of when the response was created.
     */
    public EventResponse(String eventName, int eventId, String username, String userFirstName, String userLastName, long dateOfCreation) {
        this.eventName = eventName;
        this.eventId = eventId;
        this.username = username;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.dateOfCreation = dateOfCreation;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String sprintName) {
        this.eventName = sprintName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String toString() {
        return "<" + this.eventName + " by " + this.username + ">";
    }

    public long getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(long dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }
}
