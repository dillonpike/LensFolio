package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Evidence {

    @Transient
    private static final Logger logger = LoggerFactory.getLogger(Evidence.class);

    @Id
    private int evidenceId;

    private int parentProjectId;

    private int userId;

    private String title;

    private String description;

    private Date date;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "evidence_to_tag",
            joinColumns =
            @JoinColumn(name = "evidence_id"),
            inverseJoinColumns =
            @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    /**
     * Empty constructor for JPA.
     */
    public Evidence() {}

    public Evidence(int parentProjectId, int userId, String title, String description, Date date) {
        this.parentProjectId = parentProjectId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public int getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(int evidenceId) {
        this.evidenceId = evidenceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getParentProjectId() {
        return parentProjectId;
    }

    public void setParentProjectId(int parentProjectId) {
        this.parentProjectId = parentProjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets a set of tags corresponding to the evidence.
     * @return Set of Tags.
     */
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Adds a tag to the evidence.
     * @param tag Tag to add.
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    /**
     * Remove a tag from the evidence.
     * @param tag Tag to remove.
     */
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
}
