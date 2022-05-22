package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserToGroupId implements Serializable {
    private int userId;
    private Group group;
}
