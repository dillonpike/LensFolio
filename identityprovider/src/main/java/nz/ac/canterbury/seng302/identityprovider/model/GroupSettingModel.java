package nz.ac.canterbury.seng302.identityprovider.model;
import javax.persistence.*;


@Entity
@Table(name = "group_setting_model")
public class GroupSettingModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupSettingId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupModel groupModel;


    private String alias;

    private  String gitlabApiKey;



    public int getGroupSettingId() {
        return groupSettingId;
    }

    public void setGroupSettingId(int groupSettingId) {
        this.groupSettingId = groupSettingId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getGitlabApiKey() {
        return gitlabApiKey;
    }

    public void setGitlabApiKey(String gitlabApiKey) {
        this.gitlabApiKey = gitlabApiKey;
    }

    public GroupModel getGroupModel() {
        return groupModel;
    }

    public void setGroupModel(GroupModel groupModel) {
        this.groupModel = groupModel;
    }



}
