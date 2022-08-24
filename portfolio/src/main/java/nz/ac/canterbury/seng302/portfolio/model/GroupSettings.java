package nz.ac.canterbury.seng302.portfolio.model;

import org.gitlab4j.api.GitLabApi;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity that models a group that users can join. A group is made up of a group id, short name, long name, and a
 * list of members.
 */
@Entity
@Table(name = "group_settings")
public class GroupSettings {
    /**
     * Id of the group settings.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupSettingsId;

    /**
     * Repository Id of the group.
     */
    @Column(name="repo_id")
    private long repoId;

    /**
     * Repository name of the group.
     */
    @Column(name="repo_name", length=30)
    private String repoName;

    /**
     * Repository API Key of the group.
     */
    @Column(name="repo_api_key", length=50)
    private String repoApiKey;

    /**
     * Id of the group instance the group settings relate too .
     */
    @Column(name="group_id")
    private int groupId;

    /**
     * Set of user ids of the members of the group.
     */
    @ElementCollection
    @CollectionTable(name="user_to_group", joinColumns=@JoinColumn(name="group_id"))
    @Column(name="user_id")
    private Set<Integer> memberIds = new HashSet<>();

    /**
     * GitLabApi object made with the repository id and api key, so it doesn't need to be stored in the database.
     * The transient tag stops it from being stored in the database.
     */
    @Transient
    private GitLabApi gitLabApi;

    /**
     * Empty constructor for JPA.
     */
    public GroupSettings() {}

    /**
     * Constructs a GroupSettings object.
     * @param repoId Repository id of the group.
     * @param repoName Repository name of the group.
     * @param repoApiKey Repository API Key of the group.
     * @param groupId Id of the group instance the group settings relate too .
     */
    public GroupSettings(int repoId, String repoName, String repoApiKey, int groupId) {
        this.repoId = repoId;
        this.repoName = repoName;
        this.repoApiKey = repoApiKey;
        this.groupId = groupId;
        this.gitLabApi = new GitLabApi("https://eng-git.canterbury.ac.nz", repoApiKey);
    }

    /**
     * Returns the groupSettingsId.
     * @return groupSettingsId
     */
    public int getGroupSettingsId() {
        return groupSettingsId;
    }

    /**
     * Sets the groupSettingsId.
     * @param groupSettingsId groupSettingsId
     */
    public void setGroupSettingsId(int groupSettingsId) {
        this.groupSettingsId = groupSettingsId;
    }

    /**
     * Returns the repo id.
     * @return repo id
     */
    public long getRepoId() {
        return repoId;
    }

    /**
     * Sets the repo id.
     * @param repoId repo id
     */
    public void setRepoId(long repoId) {
        this.repoId = repoId;
    }

    /**
     * Returns the repo name.
     * @return repo name
     */
    public String getRepoName() {
        return repoName;
    }

    /**
     * Sets the repo name.
     * @param repoName repo name
     */
    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    /**
     * Returns the repo API key.
     * @return repo API key
     */
    public String getRepoApiKey() {
        return repoApiKey;
    }

    /**
     * Sets the repo API key.
     * @param repoApiKey repo API key
     */
    public void setRepoApiKey(String repoApiKey) {
        this.repoApiKey = repoApiKey;
    }

    /**
     * Returns the group id.
     * @return group id
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Sets the group id.
     * @param groupId group id
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * Returns the gitLabApi object.
     * @return gitLabApi object
     */
    public GitLabApi getGitLabApi() {
        if (gitLabApi == null) {
            gitLabApi = new GitLabApi("https://eng-git.canterbury.ac.nz", repoApiKey);
        }
        return gitLabApi;
    }

}
