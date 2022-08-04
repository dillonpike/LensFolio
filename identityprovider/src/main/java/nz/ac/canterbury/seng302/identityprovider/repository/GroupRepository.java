package nz.ac.canterbury.seng302.identityprovider.repository;


import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository of Group objects.
 */
public interface GroupRepository extends CrudRepository<GroupModel, Integer> {
    GroupModel findById(int id);
    void deleteById(int id);
    GroupModel findByLongName(String groupName);
}