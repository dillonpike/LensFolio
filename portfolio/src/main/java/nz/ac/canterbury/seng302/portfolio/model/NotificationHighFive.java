package nz.ac.canterbury.seng302.portfolio.model;

/**
 * This class is used to store the parameters of the JSON list as it is passed through message mapping to the controller.
 */
public class NotificationHighFive {

    /**
     * This class should not be able to be created other than through the Javascript message mapping system.
     */
    private NotificationHighFive() {}

    /**
     * The user who is the owner of the piece of evidence being high-fived
     */
    private int sendingUserId;

    /**
     * The user who is high-fivig the piece of evidence
     */
    private int receivingUserId;

    private String sendingUserFullName;

    private String sendingEvidenceTitle;

    private int sendingEvidenceId;

    public int getSendingEvidenceId() {
        return sendingEvidenceId;
    }

    public void setSendingEvidenceId(int sendingEvidenceId) {
        this.sendingEvidenceId = sendingEvidenceId;
    }

    public String getSendingUserFullName() {
        return sendingUserFullName;
    }

    public String getSendingEvidenceTitle() {
        return sendingEvidenceTitle;
    }

    public void setSendingEvidenceTitle(String sendingEvidenceTitle) {
        this.sendingEvidenceTitle = sendingEvidenceTitle;
    }

    public void setSendingUserFullName(String sendingUserFullName) {
        this.sendingUserFullName = sendingUserFullName;
    }

    public int getSendingUserId() {
        return sendingUserId;
    }

    public void setSendingUserId(int sendingUserId) {
        this.sendingUserId = sendingUserId;
    }

    public int getReceivingUserId() {
        return receivingUserId;
    }

    public void setReceivingUserId(int receivingUserId) {
        this.receivingUserId = receivingUserId;
    }
}
