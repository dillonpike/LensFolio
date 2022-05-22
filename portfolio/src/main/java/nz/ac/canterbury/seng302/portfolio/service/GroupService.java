package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.repository.GroupRepository;
import nz.ac.canterbury.seng302.portfolio.repository.UserSortingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupService {

    /**
     * Repository of Group objects.
     */
    @Autowired
    private GroupRepository repository;

    /**
     * Getting the UserSorting Object based on the given user's id
     * @param id Integer user's Id
     * @return a UserSorting object
     */
    public Group getGroupById(Integer id) throws Exception {
        Optional<Group> group = repository.findById(id);
        if (group.isPresent()) {
            return group.get();
        } else {
            throw new Exception("Group not found");
        }
    }
}
