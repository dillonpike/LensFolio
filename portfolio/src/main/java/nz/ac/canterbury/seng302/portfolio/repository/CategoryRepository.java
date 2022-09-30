package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository of Category objects.
 */
public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Optional<Category> findById(int id);
    List<Category> findAll();
}
