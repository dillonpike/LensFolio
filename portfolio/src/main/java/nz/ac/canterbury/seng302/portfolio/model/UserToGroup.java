package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(UserToGroupId.class)
public class UserToGroup {

    @Id
    private int userId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "Group_Id")
    private Group group;

    public UserToGroup() {}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
