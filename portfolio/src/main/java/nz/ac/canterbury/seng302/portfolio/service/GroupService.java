package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Contains methods for performing operations on Group objects and storing them in the database.
 */
@Service
public class GroupService {

    /**
     * Repository of Group objects.
     */
    @Autowired
    private GroupRepository groupRepository;

    /**
     * Getting the UserSorting Object based on the given user's id
     * @param id Integer user's Id
     * @return a UserSorting object
     */
    public Group getGroupById(Integer id) throws Exception {
        Optional<Group> group = groupRepository.findById(id);
        if (group.isPresent()) {
            return group.get();
        } else {
            throw new Exception("Group not found");
        }
    }

    /**
     * Adds the user identified by the given id to the given group.
     * @param userId id of user to be added
     * @param group group user is added to
     */
    public void addMember(int userId, Group group) {
        group.addMemberId(userId);
        groupRepository.save(group);
    }

    /**
     * Removes the user identified by the given id to the given group.
     * @param userId id of user to be removed
     * @param group group user is removed from
     */
    public void removeMember(int userId, Group group) {
        group.removeMemberId(userId);
        groupRepository.save(group);
    }
}
