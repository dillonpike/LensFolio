package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;

/**
 * JPA entity that links a user to a group they're a part of.
 */
@Entity
public class UserToGroup {

    /**
     * Composite primary key made up of a user id and group id.
     */
    @EmbeddedId
    private UserToGroupId id;

    /**
     * Empty constructor for JPA.
     */
    public UserToGroup() {}

    /**
     * Constructs a UserToGroup object, which links a user to a group they're a part of.
     * @param userId id of the user
     * @param groupId id of the group the user is a member of
     */
    public UserToGroup(int userId, int groupId) {
        this.id = new UserToGroupId(userId, groupId);
    }

    /**
     * Returns the composite id of the UserToGroup object.
     * @return composite id of the UserToGroup object
     */
    public UserToGroupId getId() {
        return id;
    }

    /**
     * Sets the composite id of the UserToGroup object.
     * @param userId id of the user
     * @param groupId id of the group the user is a member of
     */
    public void setId(int userId, int groupId) {
        this.id = new UserToGroupId(userId, groupId);
    }
}
