package nz.ac.canterbury.seng302.identityprovider.service;

import java.util.ArrayList;
import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.server.GroupModelServerService;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Connects groups GRPC service and repository.
 */
@Service
public class GroupModelService {

    @Autowired
    private GroupRepository repository;

    @Autowired
    private UserModelService userModelService;

    private static final Logger logger = LoggerFactory.getLogger(GroupModelService.class);

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
        Optional<GroupModel> groupOptional = repository.findById(id);

        if (groupOptional.isPresent()) {
            // Backup the old members
            GroupModel group = groupOptional.get();
            Set<UserModel> users = Set.copyOf(group.getMembers());
            group.setMembers(new HashSet<>());
            repository.save(group);

            repository.deleteById(group.getGroupId());

            // Check to see if the user was deleted
            Optional<GroupModel> groupStillThere = repository.findById(id);
            if (groupStillThere.isPresent()) {
                // Add the users back since deleting the group did not work
                GroupModel emptyGroup = groupStillThere.get();
                emptyGroup.setMembers(users);
                repository.save(emptyGroup);
                return false;
            }
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
        Optional<GroupModel> groupOptional = repository.findById(groupId);
        if (groupOptional.isPresent()) {
            GroupModel group = groupOptional.get();
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
        Optional<GroupModel> groupOptional = repository.findById(groupId);

        if (groupOptional.isPresent()) {
            return groupOptional.get();
        } else {
            throw new InvalidAttributesException("Group does not exist!");
        }
    }

    /**
     * Checks to see if the short name given is unique in the database.
     * @param shortName Short name for group.
     * @return True if short name is unique.
     */
    public boolean checkShortNameIsUnique(String shortName) {
        Optional<GroupModel> groupOptional = repository.findByShortName(shortName);

        return groupOptional.isEmpty();
    }

    /**
     * Checks to see if the long name given is unique in the database.
     * @param longName Long name for group.
     * @return True if long name is unique.
     */
    public boolean checkLongNameIsUnique(String longName) {
        Optional<GroupModel> groupOptional = repository.findByLongName(longName);

        return groupOptional.isEmpty();
    }

    /**
     * Checks to see if the short name given is unique in the database for editing a group (doesn't consider a group
     * with the same id).
     * @param id Id of the group being edited
     * @param shortName Short name for group.
     * @return True if short name is unique.
     */
    public boolean checkShortNameIsUniqueEditing(Integer id, String shortName) {
        Optional<GroupModel> groupOptional = repository.findByShortName(shortName);

        if (groupOptional.isEmpty()) {
            return true;
        } else {
            GroupModel group = groupOptional.get();
            return id == group.getGroupId();
        }
    }

    /**
     * Checks to see if the long name given is unique in the database for editing a group (doesn't consider a group
     * with the same id).
     * @param id Id of the group being edited
     * @param longName Long name for group.
     * @return True if long name is unique.
     */
    public boolean checkLongNameIsUniqueEditing(Integer id, String longName) {
        Optional<GroupModel> groupOptional = repository.findByLongName(longName);

        if (groupOptional.isEmpty()) {
            return true;
        } else {
            GroupModel group = groupOptional.get();
            return id == group.getGroupId();
        }
    }

    /**
     * Edit and save changes of a group
     * @param groupId ID of the group being editied.
     * @param shortName New short name for group.
     * @param longName New long name for group.
     * @return Whether the new changes were saved.
     */
    public boolean editGroup(Integer groupId, String shortName, String longName) {
        Optional<GroupModel> groupOptional = repository.findById(groupId);

        if (groupOptional.isPresent()) {
            GroupModel group = groupOptional.get();
            group.setShortName(shortName);
            group.setLongName(longName);
            repository.save(group);
            return true;
        }
        return false;
    }


    /**
     * Method to retrieve every group from database
     * @return list of groups
     */
    public List<GroupModel> getAllGroups() {
        return (List<GroupModel>) repository.findAll();
    }

    /**
     * Method to convert and build a groupModel to GroupDetailsResponse.
     * @param groupModel the current groupModel
     * @return GroupDetailsResponse with current groupModel's info
     */
    public GroupDetailsResponse getGroupInfo(GroupModel groupModel) {
        GroupDetailsResponse.Builder response = GroupDetailsResponse.newBuilder();
        response.setGroupId(groupModel.getGroupId());
        response.setLongName(groupModel.getLongName());
        response.setShortName(groupModel.getShortName());
        Set<UserModel> userModelList = groupModel.getMembers();
        for (UserModel userModel : userModelList) {
            response.addMembers(userModelService.getUserInfo(userModel));
        }
        return response.build();
    }


    /**
     * Checks if a group exists by ID.
     * @param groupId ID of group being checked.
     * @return True if the group exists.
     */
    public boolean isExistById(Integer groupId) {
        return repository.existsById(groupId);
    }

    /**
     * Adds an iterable of users to a group. If a user was already part of the group, no distinction is
     * made when trying to re-add them to the group (returns true if the user was already a part of the group).
     * @param users users to be added
     * @param groupId ID of the group
     * @return Whether the user was added or not.
     */
    public boolean addUsersToGroup(Iterable<UserModel> users, Integer groupId) {
        Optional<GroupModel> groupOptional = repository.findById(groupId);
        if (groupOptional.isPresent()) {
            try {
                GroupModel group = groupOptional.get();
                for (UserModel user : users) {
                    group.addMember(user);
                }
                repository.save(group);
                logger.info(MessageFormat.format("Added the following users to group {0}: {1}", groupId, users));
                if (!groupId.equals(GroupModelServerService.MEMBERS_WITHOUT_GROUP_ID)) {
                    removeFromMembersWithoutAGroup(users);
                }
            } catch (Exception e) {
                logger.error(MessageFormat.format("Error adding user to group {0}", groupId));
                logger.error(e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Removes the given users from the special "Members without a group" group.
     * @param users users to remove
     */
    public void removeFromMembersWithoutAGroup(Iterable<UserModel> users) {
        Optional<GroupModel> groupOptional = repository.findById(GroupModelServerService.MEMBERS_WITHOUT_GROUP_ID);
        if (groupOptional.isPresent()) {
            GroupModel group = groupOptional.get();
            for (UserModel user: users) {
                group.removeMember(user);
            }
            repository.save(group);
        }
    }

    public GroupModel getMembersWithoutAGroup() {
        Optional<GroupModel> groupOptional = repository.findById(GroupModelServerService.MEMBERS_WITHOUT_GROUP_ID);
        return groupOptional.orElse(null);
    }

    /**
     * Remove users from a group. If a user was already not in the group, the method still returns true.
     * @param users iterable list of users to be removed from the group.
     * @param groupId Id of the group the users are being removed from.
     * @return Whether the user was removed from the group.
     */
    public boolean removeUsersFromGroup(Iterable<UserModel> users, Integer groupId)
        throws InvalidAttributesException {
        Optional<GroupModel> groupOptional = repository.findById(groupId);
        if (groupOptional.isPresent()) {
            GroupModel group = groupOptional.get();
            for (UserModel user : users) {
                group.removeMember(user);
                boolean noGroup = true;
                List<GroupModel> groups = getAllGroups();
                for (GroupModel newGroup : groups) {
                    if (newGroup.getGroupId() != group.getGroupId()) {
                        Set<UserModel> userModelList = newGroup.getMembers();
                        for (UserModel userModel : userModelList) {
                            if (userModel.getUserId() == user.getUserId()) {
                                noGroup = false;
                            }
                        }
                    }
                }
                if (noGroup) {
                    ArrayList<UserModel> groupuser = new ArrayList<>();
                    groupuser.add(user);
                    GroupModel g = getGroupById(GroupModelServerService.MEMBERS_WITHOUT_GROUP_ID);
                    userModelService.setOnlyGroup(groupuser,g);
                }
                // if user not part of any other groups, addUsersToGroup([user], GroupModelServerService.MEMBERS....)
            }
            repository.save(group);
            return true;
        }
        return false;
    }

    /**
     * Checks if a user is in a given group.
     * @param userId ID of the user
     * @param groupId ID of the group
     * @return True if user is in the group.
     * @throws InvalidAttributesException Thrown when the group does not exist.
     */
    public boolean isUserPartOfGroup(Integer userId, Integer groupId) throws InvalidAttributesException {
        Optional<GroupModel> groupOptional = repository.findById(groupId);

        if (groupOptional.isPresent()) {
            Set<Integer> userIds = groupOptional.get().getMemberIds();
            return userIds.contains(userId);
        } else {
            throw new InvalidAttributesException("Group does not exist!");
        }
    }
}
