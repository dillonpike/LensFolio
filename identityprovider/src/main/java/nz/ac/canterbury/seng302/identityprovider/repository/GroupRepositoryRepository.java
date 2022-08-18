package nz.ac.canterbury.seng302.identityprovider.repository;

import nz.ac.canterbury.seng302.identityprovider.model.GroupRepositoryModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Repository of Group Repository objects.
 */
@Repository
public interface GroupRepositoryRepository extends CrudRepository<GroupRepositoryModel, Integer> {
    Optional<GroupRepositoryModel> findByGroupRepositoryId (Integer id);


}
