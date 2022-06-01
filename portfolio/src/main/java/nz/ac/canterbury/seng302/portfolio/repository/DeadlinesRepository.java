package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import org.springframework.data.repository.CrudRepository;

public interface DeadlinesRepository extends CrudRepository<Deadline, Integer> {
    Deadline findById(int id);
}
