package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModelRepository;
import org.springframework.stereotype.Service;

import java.sql.Blob;
import java.util.List;

@Service
public class UserModelService {

    private final UserModelRepository repository;

    private static int userIdCount = 1;

    /**
     * Constructor of UserModelService Class
     */
    public UserModelService(UserModelRepository repository) {
        this.repository = repository;
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

}
