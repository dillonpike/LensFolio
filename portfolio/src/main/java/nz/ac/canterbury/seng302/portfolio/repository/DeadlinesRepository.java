package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.Deadlines;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface DeadlinesRepository extends CrudRepository<Deadlines, Integer> {
    Deadlines findById(int id);
}
