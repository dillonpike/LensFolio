package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GroupModelService {

    @Autowired
    private GroupRepository repository;


    /**
     * Removes the group with the given id from the database if it exists.
     * Returns true if removed, otherwise false (when the group doesn't exist).
     * @param id id of the group being removed
     * @return true if removed, otherwise false
     */
    public boolean removeGroup(Integer id) {
        Optional<GroupModel> sOptional = repository.findById(id);

        if (sOptional.isPresent()) {
            GroupModel groupUpdate = sOptional.get();
            repository.deleteById(groupUpdate.getGroupId());
            return true;
        }
        return false;
    }


    /**
     * Get all user from group by given the group ID if it exists.
     * @param groupId id of the group given
     * @return Set of user IDs
     * @throws InvalidAttributesException Thrown if group doesn't exist.
     */
    public Set<Integer> getMembersOfGroup(Integer groupId) throws InvalidAttributesException {
        Optional<GroupModel> sOptional = repository.findById(groupId);
        if (sOptional.isPresent()) {
            GroupModel group = sOptional.get();
            return group.getMemberIds();
        } else {
            throw new InvalidAttributesException("Group does not exist " + groupId);
        }
    }

    public GroupModel getGroupById(Integer groupId) throws InvalidAttributesException {
        Optional<GroupModel> sOptional = repository.findById(groupId);

        if (sOptional.isPresent()) {
            return sOptional.get();
        } else {
            throw new InvalidAttributesException("Group does not exist " + groupId);
        }
    }

}
