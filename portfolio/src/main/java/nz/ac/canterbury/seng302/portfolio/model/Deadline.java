package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Deadline {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int parentProjectId;
    private String deadlineName;
    private Date deadlineDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeadlineName() {
        return deadlineName;
    }

    public void setDeadlineName(String deadlineName) {
        this.deadlineName = deadlineName;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }



    public Deadline() {}

    public Deadline(int parentProjectId, String deadlineName, Date deadlineDate) {
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
