package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.repository.DeadlinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/***
 * Service class for saving, deleting, updating and retrieving event objects to the database.
 */
@Service
public class DeadlineService {

    @Autowired
    private DeadlinesRepository repository;

    /**
     * Updates a deadline
     * @param deadline deadline to update it to
     * @return Newly edited event
     */
    public Deadline updateDeadline(Deadline deadline) {
        Optional<Deadline> sOptional = repository.findById((Integer) deadline.getId());

        if (sOptional.isPresent()) {
            Deadline deadlineUpdate = sOptional.get();
            deadlineUpdate.setDeadlineName(deadline.getDeadlineName());
            deadlineUpdate.setDeadlineDate(deadline.getDeadlineDate());


            deadlineUpdate = repository.save(deadlineUpdate);
            return deadlineUpdate;
        } else {
            return deadline;
        }
    }

    /**
     * Get deadline by Id
     * @param id id of event
     * @return deadline with the id that is the input
     * @throws Exception If event can't be found
     */
    public Deadline getDeadlineById(Integer id) throws Exception {
        Optional<Deadline> deadline = repository.findById(id);
        if(deadline.isPresent()) {
            return deadline.get();
        } else {

            throw new Exception("Event not found");
        }
    }


    /**
     * Saves the given deadline to the database and returns it.
     * @param deadline deadline to be saved
     * @return saved deadline
     */
    public Deadline addDeadline(Deadline deadline) {
        return repository.save(deadline);
    }
}
