package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.UserToGroup;
import nz.ac.canterbury.seng302.portfolio.model.UserToGroupId;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserToGroupRepository extends CrudRepository<UserToGroup, UserToGroupId> {
    Optional<UserToGroup> findById(UserToGroupId id);
    void deleteById(UserToGroupId id);
}