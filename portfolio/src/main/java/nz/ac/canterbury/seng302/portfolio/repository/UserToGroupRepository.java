package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.UserToGroup;
import nz.ac.canterbury.seng302.portfolio.model.UserToGroupId;
import org.springframework.data.repository.CrudRepository;

public interface UserToGroupRepository extends CrudRepository<UserToGroup, Integer> {
    UserToGroup findById(int id);
    void deleteById(int id);
}