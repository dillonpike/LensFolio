package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Tags for evidence pieces.
 */
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tagId;

    private String tagName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "evidence_to_tag",
            joinColumns =
            @JoinColumn(name = "tag_id"),
            inverseJoinColumns =
            @JoinColumn(name = "evidence_id")
    )
    private Set<Evidence> evidenceWithTag = new HashSet<>();

    /**
     * Empty constructor for JPA.
     */
    public Tag() {}

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName.replaceAll(" ", "_").trim();
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    /**
     * Returns the evidence pieces with this tag.
     * @return Set of Evidence objects with this tag.
    */

    public Set<Evidence> getEvidence() {
        return evidenceWithTag;
    }

    public String toString() {
        return tagName;
    }

}
