package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.model.RolesRepository;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Blob;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserModelService {

//    private final UserModelRepository repository;
    @Autowired
    UserModelRepository repository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    UserModelRepository userModelRepository;

    private static int userIdCount = 1;

    public UserModelService(UserModelRepository userModelRepository, RolesRepository rolesRepository) {
        this.repository = userModelRepository;
        this.rolesRepository = rolesRepository;
        this.userModelRepository = userModelRepository;
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
        if (retrievedUsers.size() == 0) {
            return null;
        } else {
            return retrievedUsers.get(0);
        }
    }

    /**
     * Add new user to the database
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
            System.err.println("Edited user not saved");
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
            if (Objects.equals(role.getRoleName(), "TEACHER")) {
                return "teacher";
            } else if (Objects.equals(role.getRoleName(), "COURSE ADMINISTRATOR")) {
                return "admin";
            }
        }
        return "student";
    }

}
