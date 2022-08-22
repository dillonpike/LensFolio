package nz.ac.canterbury.seng302.portfolio.model;


/**
 * Message class that is sent when STOMP sending event updates
 */
public class NotificationMessage {

    private String artefactName;

    private int artefactId;

    private int userId;

    private String userFirstName;

    private String userLastName;

    private String username;

    private String artefactType;

    /**
     * Main constructor of an NotificationMessage
     * @param artefactName Name of the event being changed.
     * @param userId User id of the user editing.
     * @param userFirstName First name of the user editing.
     * @param userLastName Last name of the user editing.
     * @param username Username of the user editing.
     */
    public NotificationMessage(String artefactName, int artefactId, int userId, String userFirstName, String userLastName, String username, String artefactType) {
        this.artefactName = artefactName;
        this.artefactId = artefactId;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.username = username;
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

    public void setArtefactName(String sprintName) {
        this.artefactName = sprintName;
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

    public int getArtefactId() {
        return artefactId;
    }

    public void setArtefactId(int eventId) {
        this.artefactId = eventId;
    }
}
