package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Role implements Serializable {
    @Id
    private int id;

    private String roleName;

//    @ManyToMany(mappedBy = "Roles", fetch = FetchType.LAZY)
//    private Set<UserModel> users = new HashSet<>();


    public Role() { }

    public Role(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.roleName;
    }

}
