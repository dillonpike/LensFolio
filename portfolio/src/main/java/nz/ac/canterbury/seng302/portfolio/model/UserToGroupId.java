package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserToGroupId implements Serializable {
    @Column(name = "User_Id")
    private int userId;

    @Column(name = "Group_Id")
    private int groupId;

    public UserToGroupId() {
    }

    public UserToGroupId(int userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
