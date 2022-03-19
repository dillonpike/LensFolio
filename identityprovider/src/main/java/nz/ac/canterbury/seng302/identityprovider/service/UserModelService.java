package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserModelService {
    @Autowired
    private UserModelRepository repository;

    private static int userIdCount = 1;

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

    /**
     * Sets userIdCount to be the next available user id in the database.
     */
    private void findMaxUserId() {
        while(existsByUserId(userIdCount)) {
            userIdCount++;
        }
    }

}
