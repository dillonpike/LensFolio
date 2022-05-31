package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Deadlines {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private int parentProjectId;
    private String deadlineName;
    private Date deadlineDate;


    public Deadlines() {}

    public Deadlines(int id, int parentProjectId, String deadlineName, Date deadlineDate) {
        this.id = id;
        this.parentProjectId = parentProjectId;
        this.deadlineName = deadlineName;
        this.deadlineDate = deadlineDate;
    }

    @Override
    public String toString() {
        return String.format(
                "deadline[id=%d, parentProjectId='%d', deadlineName='%s', deadlineDate='%s']",
                id, parentProjectId, deadlineName, deadlineDate);
    }
}
