package nz.ac.canterbury.seng302.portfolio.model;

import java.time.Instant;
import java.util.Date;

/**
 * The response message class sent that are received by STOMP subscriptions when events are edited.
 */
public class NotificationResponse {

    private String artefactName;

    private int artefactId;

    private String username;

    private String userFirstName;

    private String userLastName;

    private long dateOfCreation;

    private String artefactType;

    private String action;

    /**
     * Main constructor for NotificationResponse
     * @param artefactName Event name for displaying.
     * @param username Username of user editing.
     * @param userFirstName First name of user editing.
     * @param userLastName Last name of user editing.
     * @param dateOfCreation Date/Time in seconds of when the response was created.
     * @param artefactType Type of artefact being edited.
     * @param action Action of the artefact. Can be "add", "edit", "delete" or "save".
     */
    public NotificationResponse(String artefactName, int artefactId, String username, String userFirstName, String userLastName, long dateOfCreation, String artefactType, String action) {
        this.artefactName = artefactName;
        this.artefactId = artefactId;
        this.username = username;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.dateOfCreation = dateOfCreation;
        this.artefactType = artefactType;
        this.action = action;
    }

    public NotificationResponse() {}

    public NotificationResponse(String username) {
        this.username = username;
    }

    public String getArtefactType() {
        return artefactType;
    }

    public void setArtefactType(String artefactType) {
        this.artefactType = artefactType;
    }

    public String getArtefactName() {
        return artefactName;
    }

    public void setArtefactName(String artefactName) {
        this.artefactName = artefactName;
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

    public int getArtefactId() {
        return artefactId;
    }

    public void setArtefactId(int eventId) {
        this.artefactId = eventId;
    }

    public String toString() {
        return "<" + this.artefactName + " by " + this.username + ">";
    }

    public long getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(long dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Converts a NotificationMessage to a NotificationResponse. Gives it a relevant action and date of creation.
     * @param message The NotificationMessage to convert.
     * @param action The action to give the NotificationResponse.
     * @return The converted NotificationResponse.
     */
    public static NotificationResponse fromMessage(NotificationMessage message, String action) {
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        return new NotificationResponse(message.getArtefactName(), message.getArtefactId(), message.getUsername(), message.getUserFirstName(), message.getUserLastName(), dateOfNotification, message.getArtefactType(), action);
    }
}
