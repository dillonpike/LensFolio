package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.model.UserSortingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Contains methods for saving and retrieving UserSorting objects to the database.
 */
@Service
public class UserSortingService {

    /**
     * Repository of UserSorting objects.
     */
    @Autowired
    private UserSortingRepository repository;

    /**
     * Check if a user has set up sorting method by checking if there is entity in the 'user_sorting' table with User_Id same as the id of the user
     * if entity is found, then update the Column_Index in the database with the new one, and also the Sort_Order as well
     * Otherwise, do nothing( save the current sorting method
     * @param userSorting UserSorting object which contains the information of the new sorting method chosen by the user
     * @return a UserSorting object which either the updated one or the non-updated one
     */
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

    /**
     * Getting the UserSorting Object based on the given user's id
     * @param id Integer user's Id
     * @return a UserSorting object
     */
    public UserSorting getUserSortingById(Integer id) throws Exception {
        Optional<UserSorting> userSorting = repository.findById(id);
        if (userSorting.isPresent()) {
            return userSorting.get();
        } else {
            throw new Exception("UserSorting not found");
        }
    }
}
