package nz.ac.canterbury.seng302.portfolio.model;

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

    /**
     * Main constructer for NotificationResponse
     * @param artefactName Event name for displaying.
     * @param username Username of user editing.
     * @param userFirstName First name of user editing.
     * @param userLastName Last name of user editing.
     * @param dateOfCreation Date/Time in seconds of when the response was created.
     */
    public NotificationResponse(String artefactName, int artefactId, String username, String userFirstName, String userLastName, long dateOfCreation, String artefactType) {
        this.artefactName = artefactName;
        this.artefactId = artefactId;
        this.username = username;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.dateOfCreation = dateOfCreation;
        this.artefactType = artefactType;
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
}
