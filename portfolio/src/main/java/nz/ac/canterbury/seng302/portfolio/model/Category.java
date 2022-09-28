package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;

    private String categoryName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "evidence_to_category",
            joinColumns =
            @JoinColumn(name = "category_id"),
            inverseJoinColumns =
            @JoinColumn(name = "evidence_id")
    )
    private Set<Evidence> evidenceWithCategory = new HashSet<>();

    /**
     * Empty constructor for JPA.
     */
    public Category() {}

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName.trim().replaceAll(" ", "_");
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Returns the evidence pieces with this Category.
     * @return Set of Evidence objects with this Category.
     */
    public Set<Evidence> getEvidence() {
        return evidenceWithCategory;
    }

    public String toString() {
        return categoryName;
    }

    /*
     * Adds a new piece of evidence to the HashSet.
     * @param evidence The evidence to add.
     */
    public void addEvidence(Evidence evidence) {
        this.evidenceWithCategory.add(evidence);
    }
}
