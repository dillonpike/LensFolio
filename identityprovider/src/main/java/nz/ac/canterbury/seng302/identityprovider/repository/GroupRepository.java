package nz.ac.canterbury.seng302.identityprovider.repository;

import nz.ac.canterbury.seng302.identityprovider.model.Group;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository of Group objects.
 */
public interface GroupRepository extends CrudRepository<Group, Integer> {
    Group findById(int id);
    void deleteById(int id);
}