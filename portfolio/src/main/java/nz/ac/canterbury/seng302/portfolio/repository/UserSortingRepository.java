package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository of UserSorting objects.
 */
public interface UserSortingRepository extends CrudRepository<UserSorting, Integer> {
    UserSorting findById(int id);
    void deleteById(int id);
}
