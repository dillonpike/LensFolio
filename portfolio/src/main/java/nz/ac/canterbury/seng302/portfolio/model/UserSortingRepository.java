package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository of UserSorting objects.
 */
public interface UserSortingRepository extends CrudRepository<UserSorting, Integer> {
    UserSorting findById(int id);
    void deleteById(int id);
}
