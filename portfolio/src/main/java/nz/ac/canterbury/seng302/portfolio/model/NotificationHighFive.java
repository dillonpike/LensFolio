package nz.ac.canterbury.seng302.portfolio.model;

public class NotificationHighFive {

    private NotificationHighFive() {}

    private int sendingUserId;

    // TODO Change to a list as multiple users can be connected to one.
    private int receivingUserId;

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
