package nz.ac.canterbury.seng302.portfolio.model;

public class TwoGroup {

    private int sendingGroupId;

    private int receivingGroupId;

    public int getReceivingGroupId() {
        return receivingGroupId;
    }

    public void setReceivingGroupId(int receivingGroupId) {
        this.receivingGroupId = receivingGroupId;
    }

    public int getSendingGroupId() {
        return sendingGroupId;
    }

    public void setSendingGroupId(int sendingGroupId) {
        this.sendingGroupId = sendingGroupId;
    }
}
