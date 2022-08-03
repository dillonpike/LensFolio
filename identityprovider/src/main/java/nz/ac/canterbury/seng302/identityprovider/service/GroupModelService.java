package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.util.Optional;
import java.util.Set;

/**
 * Connects groups GRPC service and repository.
 */
@Service
public class GroupModelService {

    @Autowired
    private GroupRepository repository;

    /**
     * Adds a group to the database
     * Returns saved groupModel object
     * @param shortName the groups short name
     * @param longName the groups long name
     * @param courseId the id of the course
     * @return The group saved in the database
     */
    public GroupModel addGroup(String shortName, String longName, Integer courseId) {
        GroupModel group = new GroupModel(shortName, longName, courseId);
        return repository.save(group);
    }


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

    /**
     * Get a group by its group ID.
     * @param groupId ID of group.
     * @return group as GroupModel
     * @throws InvalidAttributesException Thrown if the group does not exist.
     */
    public GroupModel getGroupById(Integer groupId) throws InvalidAttributesException {
        Optional<GroupModel> sOptional = repository.findById(groupId);

        if (sOptional.isPresent()) {
            return sOptional.get();
        } else {
            throw new InvalidAttributesException("Group does not exist " + groupId);
        }
    }

    /**
     * Checks to see if the short name given is unique in the database.
     * @param shortName Short name for group.
     * @return True if short name is unique.
     */
    public boolean checkShortNameIsUnique(String shortName) {
        Optional<GroupModel> sOptional = repository.findByShortName(shortName);

        return sOptional.isEmpty();
    }

    /**
     * Checks to see if the long name given is unique in the database.
     * @param longName Long name for group.
     * @return True if long name is unique.
     */
    public boolean checkLongNameIsUnique(String longName) {
        Optional<GroupModel> sOptional = repository.findByLongName(longName);

        return sOptional.isEmpty();
    }

}
