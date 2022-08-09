package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity that models a group that users can join. A group is made up of a group id, short name, long name, and a
 * list of members.
 */
@Entity
@Table(name = "group_model",
        uniqueConstraints={
                @UniqueConstraint(columnNames = {"short_Name", "course_Id"}),
                @UniqueConstraint(columnNames = {"long_Name", "course_Id"})
        })
public class GroupModel {

    /**
     * ID of the group.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupId;

    /**
     * Short name of the group.
     */
    @Column(name="short_name", length=15)
    private String shortName;

    /**
     * Long name of the group.
     */
    @Column(name="long_name", length=50)
    private String longName;

    /**
     * ID of the course instance the group is a part of.
     */
    @Column(name="course_id")
    private int courseId;

    /**
     * Set of user ids of the members of the group.
     */
    @ElementCollection
    @CollectionTable(name="user_to_group", joinColumns=@JoinColumn(name="group_id"))
    @Column(name="User_Id")
    private Set<Integer> memberIds = new HashSet<>();


    /**
     * Set of repositories that this group has.
     */
    @OneToMany(mappedBy = "groupModel")
    private Set<GroupRepositoryModel> groupRepositoryModels;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_to_group",
            joinColumns =
            @JoinColumn(name = "group_id"),
            inverseJoinColumns =
            @JoinColumn(name = "User_Id")
    )
    private Set<UserModel> users = new HashSet<>();

    /**
     * Empty constructor for JPA.
     */
    public GroupModel() {}

    /**
     * Constructs a Group object.
     * @param shortName short name of the group
     * @param longName long name of the group
     * @param courseId ID of the course instance the group is a part of
     */
    public GroupModel(String shortName, String longName, int courseId) {
        this.shortName = shortName;
        this.longName = longName;
        this.courseId = courseId;
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
     * Returns the group's short name.
     * @return group's short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the group's short name.
     * @param shortName group's short name
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Returns the group's long name.
     * @return group's long name
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Sets the group's long name.
     * @param longName group's long name
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     * Returns the id of the course instance the group is a part of.
     * @return id of the course instance the group is a part of
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * Sets the id of the course instance the group is a part of.
     * @param courseId id of the course instance the group is a part of
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * Returns the ids of the users a part of the group.
     * @return ids of the users a part of the group
     */
    public Set<Integer> getMemberIds() {
        return users.stream().map(UserModel::getUserId).collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    /**
     * Returns the users a part of the group.
     * @return users a part of the group
     */
    public Set<UserModel> getMembers() {
    public void removeMember(int userId) {
        memberIds.remove(userId);
    }

    /**
     * Get all the repositories that a group has
     * @return set of GroupRepositoryModel object
     */
    public Set<GroupRepositoryModel> getGroupRepositoryModels() {
        return groupRepositoryModels;
    }

    /**
     * Set all the repositories that a group has
     */
    public void setGroupRepositoryModels(Set<GroupRepositoryModel> groupRepositoryModels) {
        this.groupRepositoryModels = groupRepositoryModels;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    /**
     * Sets the members of the group. Overwrites the old members, so be careful using this method.
     * @param members set of users to add
     */
    public void setMembers(Set<UserModel> members) {
        this.users = members;
    }

    /**
     * Adds the given user to the group.
     * @param user user to add to the group
     */
    public void addMember(UserModel user) {
        users.add(user);
    }

    /**
     * Removes the user from the group.
     * @param user user to remove from the group
     */
    public void removeMember(UserModel user) {
        users.removeIf(userInGroup -> userInGroup.getUserId() == user.getUserId());
    }
}
