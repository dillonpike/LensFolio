package nz.ac.canterbury.seng302.identityprovider.model;
import javax.persistence.*;

/**
 * JPA entity that models a group setting that users who belong to that group can read and edit. A group setting model is made up of a group setting id, group id,
 * alias, and gitlab api key.
 */
@Entity
@Table(name = "group_repository_model")
public class GroupRepositoryModel {

    /**
     * ID of the group setting.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupRepositoryId;

    /**
     * Many-to-one relationship with the group model instance from group_model table.
     * Group model that the setting/repo belongs to
     */
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupModel groupModel;

    /**
     * Alias of the group repository
     */
    private String alias;

    /**
     * gitlab API key that is used to connect the repository in Gitlab
     */
    private  String gitlabApiKey;

    /**
     * Constructs a Group Repository Model object.
     * @param groupModel group object that this repo belongs to
     * @param alias name of the group repository
     * @param gitlabApiKey gitlab API key to connect the real repository in gitlab
     */
    public GroupRepositoryModel(GroupModel groupModel, String alias, String gitlabApiKey) {
        this.groupModel = groupModel;
        this.alias = alias;
        this.gitlabApiKey = gitlabApiKey;
    }

    /**
     * Empty constructor for JPA.
     */
    public GroupRepositoryModel() {}


    /**
     * Returns the group repository id.
     * @return group repository's id
     */
    public int getGroupRepositoryId() {
        return groupRepositoryId;
    }

    /**
     * Sets the group repository's id
     */
    public void setGroupRepositoryId(int groupRepositoryId) {
        this.groupRepositoryId = groupRepositoryId;
    }

    /**
     * Returns the group repository's alias
     * @return group repository's alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the group repository's id
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Returns the group repository's gitlab API Key.
     * @return group repository's id
     */
    public String getGitlabApiKey() {
        return gitlabApiKey;
    }

    /**
     * Sets the group repository's gitlab API key
     * this api key is used to connect to the application with the repository in gitlab
     */
    public void setGitlabApiKey(String gitlabApiKey) {
        this.gitlabApiKey = gitlabApiKey;
    }

    /**
     * Returns the group object that this repository belongs to.
     * @return group object which the repo belongs to
     */
    public GroupModel getGroupModel() {
        return groupModel;
    }

    /**
     * Sets the group repository's Group Model Object which this repository belongs to
     */
    public void setGroupModel(GroupModel groupModel) {
        this.groupModel = groupModel;
    }



}
