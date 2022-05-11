package nz.ac.canterbury.seng302.identityprovider.repository;

import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends CrudRepository<Roles, Integer> {

    Roles findByRoleName(String RoleName);
}
