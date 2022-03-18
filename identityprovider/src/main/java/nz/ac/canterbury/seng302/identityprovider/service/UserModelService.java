package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserModelService {
    @Autowired
    private UserModelRepository repository;

    private static int userIdCount = 1;

    public UserModel getUserById(int userId) {
        return (UserModel) repository.findByUserId(userId);
    }

    @Transactional
    public UserModel addUser(UserModel user) {
        //user.setUserId(userIdCount);
        //userIdCount++;
        System.out.println(user);
        return repository.save(user);
    }
}
