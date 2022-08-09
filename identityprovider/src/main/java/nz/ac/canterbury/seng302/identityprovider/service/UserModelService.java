package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.repository.RolesRepository;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.UserModelRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service
public class UserModelService {

    private static final Logger logger = LoggerFactory.getLogger(UserModelService.class);

    @Autowired
    private UserModelRepository repository;

    @Autowired
    private RolesRepository rolesRepository;

    private static int userIdCount = 1;

    public UserModelService(UserModelRepository userModelRepository, RolesRepository rolesRepository, GroupRepository groupRepository) {
        this.repository = userModelRepository;
        this.rolesRepository = rolesRepository;
    }

    /**
     * Get User entity from database with the given id
     * @param userId user's id which used to search user entity in database
     * @return UserModel user entity retrieved from database
     */
    public UserModel getUserById(int userId) {
        return repository.findByUserId(userId);
    }

    /**
     * Returns all users from a list of user ids.
     * @param userIds List of user ids to get.
     * @return Iterable list of UserModels with ids of given list.
     */
    public Iterable<UserModel> getUsersByIds(List<Integer> userIds) {
        return repository.findAllById(userIds);
    }

    /**
     * Check if there is a user entity with the given id
     * @param userId user's id which used to search user entity in database
     * @return true if user with given id exist in database, false otherwise
     */
    public boolean existsByUserId(int userId) {
        return repository.existsByUserId(userId);
    }

    /**
     * Get the user entity with the given username
     * @param username username input which used to search a user entity in database
     * @return user object retrieved from the database
     */
    public UserModel getUserByUsername(String username) {
        List<UserModel> retrievedUsers = repository.findByUsername(username);
        if (retrievedUsers.isEmpty()) {
            return null;
        } else {
            return retrievedUsers.get(0);
        }
    }

    /**
     * Add new user to the database. Makes sure they have the default student role
     * and are added to the 'members without a group' group.
     * @param user contains all data of the user that will be persisted in database
     * @return UserModel object which is the saved entity
     */
    public UserModel addUser(UserModel user) {
        findMaxUserId();
        user.setUserId(userIdCount);
        userIdCount++;
        Roles studentRole = rolesRepository.findByRoleName("STUDENT");
        user.addRoles(studentRole);
        return repository.save(user);
    }


    /**
     * Update the user account information to the database
     * @param user contains all new data of a user that will be persisted in database
     * @return true if update transaction success, false otherwise
     */
    public boolean saveEditedUser(UserModel user) {
        boolean status;
        try{
            repository.save(user);
            status = true;
        } catch(Exception e) {
            status = false;
            logger.error("Edited user not saved");
        }
        return status;
    }


    /**
     * Sets userIdCount to be the next available user id in the database.
     */
    private void findMaxUserId() {
        while(existsByUserId(userIdCount)) {
            userIdCount++;
        }
    }

    /***
     * Retrieves every user from database
     * @return all user
     */
    public List<UserModel> findAllUser() {
        return (List<UserModel>) repository.findAll();
    }


    /***
     * Method to get the user's highest role
     * @param user current user
     * @return highest role
     */
    public String getHighestRole(UserModel user) {
        Set<Roles> roles = user.getRoles();
        for (Roles role : roles) {
            if (Objects.equals(role.getRoleName(), "COURSE ADMINISTRATOR")) {
                return "admin";
            }
        }
        for (Roles role : roles) {
            if (Objects.equals(role.getRoleName(), "TEACHER")) {
                return "teacher";
            }
        }
        return "student";
    }

    /**
     * Get list of users from list of user IDs as UserResponse's.
     * @param userIds List of user ids to convert.
     * @return List of users as UserResponse's.
     */
    public List<UserResponse> getUserInformationByList(Set<Integer> userIds) {
        List<UserResponse> userResponseList = new ArrayList<>();
        for (Integer userId : userIds) {
            UserModel user = getUserById(userId);
            userResponseList.add(getUserInfo(user));
        }
        return userResponseList;
    }

    /***
     * Help method to get user's information as a User Model
     * @param user User model
     * @return User model
     */
    public UserResponse getUserInfo(UserModel user) {
        UserResponse.Builder response = UserResponse.newBuilder();
        response.setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setNickname(user.getNickname())
                .setId(user.getUserId());
        Set<Roles> roles = user.getRoles();
        Roles[] rolesArray = roles.toArray(new Roles[roles.size()]);

        for (Roles value : rolesArray) {
            response.addRolesValue(value.getId());
        }
        return response.build();
    }

    /**
     * Checks to see if the user has the teacher role. If not, it adds the role to the user.
     * @param user user to check if they are in the teachers group.
     */
    public boolean checkUserHasTeacherRole(UserModel user) {
        Roles teacherRole = rolesRepository.findByRoleName("TEACHER");

        boolean addedRole = false;
        if (!user.getRoles().contains(teacherRole)) {
            user.addRoles(teacherRole);
            addedRole = saveEditedUser(user);
        }
        return addedRole;
    }

    public void setOnlyGroup(Iterable<UserModel> users, GroupModel group) {
        for (UserModel user: users) {
            user.setGroups(Set.of(group));
        }
        repository.saveAll(users);
    }

    /**
     * Adds user to 'users without a group' group if they are not in any other groups.
     * @param usersWithoutGroupGroup 'users without a group' group
     */
    public void usersAddedToUsersWithoutGroup(GroupModel usersWithoutGroupGroup) {
        Iterable<UserModel> users = repository.findAll();
        for (UserModel user : users) {
            Set<GroupModel> usersGroups = user.getGroups();
            if (usersGroups.isEmpty()) {
                user.addGroup(usersWithoutGroupGroup);
                repository.save(user);
                logger.info(MessageFormat.format("New user id:{0} found without group, added them to 'users without a group' group", user.getUserId()));
            }
        }
    }

}
