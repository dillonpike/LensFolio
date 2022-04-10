package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.model.UserSortingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSortingService {

    @Autowired
    private UserSortingRepository repository;

    public UserSorting updateUserSorting(UserSorting userSorting) {
        Optional<UserSorting> sOptional = repository.findById((Integer) userSorting.getUserId());

        if (sOptional.isPresent()) {
            UserSorting sortUpdate = sOptional.get();
            sortUpdate.setColumnIndex(userSorting.getColumnIndex());
            sortUpdate.setSortOrder(userSorting.getSortOrder());

            sortUpdate = repository.save(sortUpdate);
            return sortUpdate;
        }
        else {
            userSorting = repository.save(userSorting);
            return userSorting;
        }
    }

    public UserSorting getUserSortingById(Integer id) throws Exception {
        Optional<UserSorting> userSorting = repository.findById(id);
        if (userSorting.isPresent()) {
            return userSorting.get();
        } else {
            throw new Exception("UserSorting not found");
        }
    }
}
