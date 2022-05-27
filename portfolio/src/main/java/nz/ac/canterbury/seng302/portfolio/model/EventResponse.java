package nz.ac.canterbury.seng302.portfolio.model;

/**
 * The response message class sent that are received by STOMP subscriptions when events are edited.
 */
public class EventResponse {

    private String eventName;

    private String username;

    private String userFirstName;

    private String userLastName;

    /**
     * Main constructer for EventResponse
     * @param eventName Event name for displaying.
     * @param username Username of user editing.
     * @param userFirstName First name of user editing.
     * @param userLastName Last name of user editing.
     */
    public EventResponse(String eventName, String username, String userFirstName, String userLastName) {
        this.eventName = eventName;
        this.username = username;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
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
}
