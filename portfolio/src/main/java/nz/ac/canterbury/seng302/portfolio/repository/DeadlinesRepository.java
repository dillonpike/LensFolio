package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeadlinesRepository extends CrudRepository<Deadline, Integer> {
    Deadline findById(int id);

    List<Deadline> findAllByOrderByDeadlineDate();
}
