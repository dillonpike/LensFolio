package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA entity that models a group that users can join.
 */
@Entity
@Table(name = "group_model",
        uniqueConstraints={
        @UniqueConstraint(columnNames = {"Short_Name", "Course_Id"}),
        @UniqueConstraint(columnNames = {"Long_Name", "Course_Id"})
})
public class Group {

    /**
     * Id of the group.
     */
    @Id
    private int groupId;

    /**
     * Short name of the group.
     */
    @Column(name="Short_Name")
    private String shortName;

    /**
     * Long name of the group.
     */
    @Column(name="Long_Name")
    private String longName;

    /**
     * Id of the course instance the group is a part of.
     */
    @Column(name="Course_Id")
    private int courseId;

    /**
     * Set of UserToGroup objects that map users to the group.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "Group_Id")
    private Set<UserToGroup> members = new HashSet<>();

    /**
     * Empty constructor for JPA.
     */
    public Group() {}

    /**
     * Constructs a Group object.
     * @param groupId id of the group
     * @param shortName short name of the group
     * @param longName long name of the group
     * @param courseId Id of the course instance the group is a part of
     */
    public Group(int groupId, String shortName, String longName, int courseId) {
        this.groupId = groupId;
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
        return members.stream().map(x -> x.getId().getUserId()).collect(Collectors.toSet());
    }
}
