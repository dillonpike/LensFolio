package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
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
        this.tagName = tagName.trim().replaceAll(" ", "_");
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

    public void setEvidence(Set<Evidence> evidence) {
        this.evidenceWithTag = evidence;
    }

    public String toString() {
        return tagName;
    }

    /**
     * Overridden to consider tags with the same name to have the same hash, for removing duplicate tags.
     * @return hash code of tag
     */
    @Override
    public int hashCode() {
        return Objects.hash(tagName);
    }

    /**
     * Overridden to consider tags with the same name to be equal, for removing duplicate tags.
     * @param o object to compare to
     * @return true if objects are equal, otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return this.tagName.equals(tag.tagName);
    }
}
