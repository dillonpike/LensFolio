package nz.ac.canterbury.seng302.portfolio.model;

public class NotificationHighFive {

    private NotificationHighFive() {}

    private int sendingUserId;

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
