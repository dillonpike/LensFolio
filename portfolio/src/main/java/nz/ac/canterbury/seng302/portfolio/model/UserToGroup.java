package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;

@Entity
public class UserToGroup {

    @EmbeddedId
    private UserToGroupId id;

    public UserToGroup() {}

    public UserToGroup(int userId, int groupId) {
        this.id = new UserToGroupId(userId, groupId);
    }

    public UserToGroupId getId() {
        return id;
    }

    public void setId(UserToGroupId id) {
        this.id = id;
    }
}
