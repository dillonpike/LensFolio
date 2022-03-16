package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "RoleModel")
public class RoleModel {
    @Id
    @Column(name = "id_role")
    private Long roleId;

    @ManyToMany
    @JoinColumn(name = "id_user")
    private List<UserModel> users;

    private String roleName;


}
