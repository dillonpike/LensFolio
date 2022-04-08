package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Roles implements Serializable {
    @Id
    private int id;

    private String roleName;

    public Roles() { }

    public Roles(int id, String roleName) {
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
