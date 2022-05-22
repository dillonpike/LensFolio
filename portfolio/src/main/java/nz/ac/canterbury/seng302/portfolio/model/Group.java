package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "group_model",
        uniqueConstraints={
        @UniqueConstraint(columnNames = {"Short_Name", "Course_Id"}),
        @UniqueConstraint(columnNames = {"Long_Name", "Course_Id"})
})
public class Group {

    @Id
    private int groupId;

    @Column(name="Short_Name")
    private String shortName;

    @Column(name="Long_Name")
    private String longName;

    @Column(name="Course_Id")
    private int courseId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "Group_Id")
    private Set<UserToGroup> memberIds = new HashSet<>();

    public Group() {}

    public Group(int groupId, String shortName, String longName, int courseId) {
        this.groupId = groupId;
        this.shortName = shortName;
        this.longName = longName;
        this.courseId = courseId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Set<Integer> getMemberIds() {
        return memberIds.stream().map(UserToGroup::getUserId).collect(Collectors.toSet());
    }

    public void setMemberIds(Set<UserToGroup> memberIds) {
        this.memberIds = memberIds;
    }
}
