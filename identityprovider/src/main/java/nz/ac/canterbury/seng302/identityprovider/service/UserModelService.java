package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserModelService {
    @Autowired
    private UserModelRepository repository;

    public UserModel getUserById(int userId) {
        UserModel user = (UserModel) repository.findByUserId(userId);
        return user;
    }
}
