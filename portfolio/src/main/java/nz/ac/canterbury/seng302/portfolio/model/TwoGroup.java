package nz.ac.canterbury.seng302.portfolio.model;

/**
 * This class is used for sending 2 group ids to do with changing members of said groups. It is used in the websocket
 * implementation for reloading the respective user pages when they have selected either group.
 */
public class TwoGroup {

    private TwoGroup() {}

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
