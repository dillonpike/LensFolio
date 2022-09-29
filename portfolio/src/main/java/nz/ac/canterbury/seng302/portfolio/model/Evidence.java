package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evidence is associated with a user and contains a title, description, and a date.
 * It also will have a set of tags.
 */
@Entity
public class Evidence {

    @Transient
    private static final Logger logger = LoggerFactory.getLogger(Evidence.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int evidenceId;

    private int parentProjectId;

    private int userId;

    private String title;

    private String description;

    private Date date;

    /**
     * The tags associated with this evidence.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "evidence_to_tag",
            joinColumns =
            @JoinColumn(name = "evidence_id"),
            inverseJoinColumns =
            @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    /**
     * The categories associated with this evidence.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "evidence_to_category",
            joinColumns =
            @JoinColumn(name = "evidence_id"),
            inverseJoinColumns =
            @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    /**
     * The Weblinks associated with this evidence.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "evidence_to_weblink",
            joinColumns =
            @JoinColumn(name = "evidence_id"),
            inverseJoinColumns =
            @JoinColumn(name = "weblink_id")
    )
    private Set<WebLink> webLinks = new HashSet<>();

    /**
     * The users that have high fived this piece of evidence.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "evidence_to_highfivers",
            joinColumns =
            @JoinColumn(name = "evidence_id"),
            inverseJoinColumns =
            @JoinColumn(name = "high_fivers_id")
    )
    private Set<HighFivers> highFivers = new HashSet<>();

    @Transient
    private Set<Integer> highFiverIds = new HashSet<>();


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

    public Date getDate() { return date; }

    /**
     * Returns evidence date as a string in the following format: 26 September 2022
     * @return formatted evidence date string
     */
    public String getDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return dateFormat.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * FOR JAVASCRIPT USE ONLY. Please use addSkillTags() and removeSkillTags() instead.
     * Sets the tags associated with this evidence.
     * @param tags new set of tags.
     */
    public void setTags(Set<String> tags) {
        Set<Tag> finalTags = new HashSet<>();
        for (String tag : tags) {
            finalTags.add(new Tag(tag));
        }
        this.tags = finalTags;
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

    /**
     * FOR JAVASCRIPT USE ONLY. Please use addCategories() and removeCategories() instead.
     * Sets the categories associated with this evidence.
     * @param categories new set of categories.
     */
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    /**
     * Gets a set of tags corresponding to the evidence.
     * @return Set of categories.
     */
    public Set<Category> getCategories() {
        return categories;
    }

    /**
     * Adds a category to the evidence.
     * @param category Category to add.
     */
    public void addCategory(Category category) {
        this.categories.add(category);
    }

    /**
     * Remove a category from the evidence.
     * @param category Category to remove.
     */
    public void removeCategory(Category category) {
        this.categories.remove(category);
    }

    /**
     * FOR JAVASCRIPT USE ONLY. Please use addWebLinks() and removeWebLinks() instead.
     * Sets the weblinks associated with this evidence.
     * @param webLinks new set of weblinks.
     */
    public void setWebLinks(Set<WebLink> webLinks) {
        this.webLinks = webLinks;
    }

    /**
     * Gets a set of weblinks corresponding to the evidence.
     * @return HashSet of WebLinks.
     */
    public Set<WebLink> getWebLinks() {
        return webLinks;
    }

    /**
     * Adds a weblink to the evidence.
     * @param webLink New weblink to add.
     */
    public void addWebLink(WebLink webLink) {
        this.webLinks.add(webLink);
    }

    /**
     * Remove a weblink from the evidence. Does nothing if the weblink is not in the evidence.
     * @param webLink Weblink to remove.
     */
    public void removeWebLink(WebLink webLink) {
        this.webLinks.remove(webLink);
    }

    /**
     * Gets the user ids of users that have high fived this piece of evidence.
     * @return Set of user ids.
     */
    public Set<Integer> getHighFiverIds() {
        highFiverIds.addAll(highFivers.stream().map(HighFivers::getUserId).collect(Collectors.toSet()));
        return highFiverIds;
    }

    /**
     * Set list of HighFivers Object to piece of evidence
     * @param highFivers the list of HighFivers object that relates to piece of evidence
     */
    public void setHighFivers(Set<HighFivers> highFivers) {
        this.highFivers = highFivers;
    }

    /**
     * Add an HighFivers Object to piece of evidence
     * @param highFiver an HighFivers object
     */
    public void addHighFivers(HighFivers highFiver) {
        this.highFivers.add(highFiver);
    }

    /**
     * Remove an HighFivers Object from piece of evidence
     * @param highFiver an HighFivers object to remove
     */
    public void removeHighFivers(HighFivers highFiver) {
        this.highFivers.remove(highFiver);
    }

    /**
     * Remove an HighFivers Object from piece of evidence by user id
     * @param highFiverId a user id to remove
     */
    public void removeHighFiversById(int highFiverId) {
        for (HighFivers highFiver : highFivers) {
            if (highFiver.getUserId() == highFiverId) {
                this.highFivers.remove(highFiver);
                break;
            }
        }
    }

    /**
     * returns list of users that have given a High Five to a piece of evidence
     */
    public Set<HighFivers> getHighFivers() {
        return highFivers;
    }
}
