package nz.ac.canterbury.seng302.identityprovider.repository;


import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository of Group objects.
 */
@Repository
public interface GroupRepository extends CrudRepository<GroupModel, Integer> {
    GroupModel findById(int id);
    void deleteById(int id);
    GroupModel findByGroupId(int groupId);
    Optional<GroupModel> findByLongName(String groupName);
    Optional<GroupModel> findByShortName(String shortName);
    GroupModel getGroupModelByGroupId(int groupId);
}
