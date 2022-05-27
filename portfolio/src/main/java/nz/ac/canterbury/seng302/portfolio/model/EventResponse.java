package nz.ac.canterbury.seng302.portfolio.model;

/**
 * The response message class sent that are received by STOMP subscriptions when events are edited.
 */
public class EventResponse {

    private String eventName;

    private int eventId;

    /**
     * Main constructer for EventResponse
     * @param eventId Id of the event
     * @param eventName Event name for displaying
     */
    public EventResponse(int eventId, String eventName) {
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
