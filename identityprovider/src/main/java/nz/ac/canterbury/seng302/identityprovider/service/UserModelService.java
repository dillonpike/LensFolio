package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserModelService {

    private final UserModelRepository repository;

    private static int userIdCount = 1;

    public UserModelService(UserModelRepository repository) {
        this.repository = repository;
    }

    public UserModel getUserById(int userId) {
        return repository.findByUserId(userId);
    }

    public boolean existsByUserId(int userId) {
        return repository.existsByUserId(userId);
    }

    public UserModel getUserByUsername(String username) {
        List<UserModel> retrievedUsers = repository.findByUsername(username);
        if (retrievedUsers.size() == 0) {
            return null;
        } else {
            return retrievedUsers.get(0);
        }
    }


    public UserModel addUser(UserModel user) {
        findMaxUserId();
        user.setUserId(userIdCount);
        userIdCount++;
        return repository.save(user);
    }

    public boolean saveEditedUser(UserModel user) {
        boolean status;
        try{
            repository.save(user);
            status = true;
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
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