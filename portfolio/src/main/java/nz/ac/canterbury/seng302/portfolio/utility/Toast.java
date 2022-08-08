package nz.ac.canterbury.seng302.portfolio.utility;

public class Toast {

    private String artefactInformation;
    private String artefactName;
    private Integer artefactId;
    private String Username;
    private String userFirstName;
    private String userLastName;

    public Toast() {}

    public Toast(String artefactInformation, String artefactName, Integer artefactId, String username, String userFirstName, String userLastName) {
        this.artefactInformation = artefactInformation;
        this.artefactName = artefactName;
        this.artefactId = artefactId;
        Username = username;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
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
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
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
