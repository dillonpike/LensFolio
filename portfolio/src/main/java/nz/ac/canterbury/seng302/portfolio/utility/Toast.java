package nz.ac.canterbury.seng302.portfolio.utility;

/**
 * Contains getters and setters for Toast object.
 */
public class Toast {

    private String artefactInformation;
    private String artefactName;
    private Integer artefactId;
    private String username;
    private String userFirstName;
    private String userLastName;
    private String action;

    public Toast() {}

    /**
     * Toast constructor.
     */
    public Toast(String artefactInformation, String artefactName, Integer artefactId, String username, String userFirstName, String userLastName, String action) {
        this.artefactInformation = artefactInformation;
        this.artefactName = artefactName;
        this.artefactId = artefactId;
        this.username = username;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.action = action;
    }

    public String getArtefactInformation() {
        return artefactInformation;
    }

    public void setArtefactInformation(String artefactInformation) {
        this.artefactInformation = artefactInformation;
    }

    public String getArtefactName() {
        return artefactName;
    }

    public void setArtefactName(String artefactName) {
        this.artefactName = artefactName;
    }

    public Integer getArtefactId() {
        return artefactId;
    }

    public void setArtefactId(Integer artefactId) {
        this.artefactId = artefactId;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
