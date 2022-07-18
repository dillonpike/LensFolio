package nz.ac.canterbury.seng302.portfolio.service;

import java.util.ArrayList;
import java.util.Arrays;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.repository.DeadlinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * Get list of all deadlines
     * @return List of deadlines
     */
    public List<Deadline> getAllDeadlines() {
        return (List<Deadline>) repository.findAll();
    }

    /**
     * Get list of all deadlines ordered by date
     * @return List of deadlines ordered by date
     */
    public List<Deadline> getAllDeadlinesOrdered() {
        return repository.findAllByOrderByDeadlineDate();
    }


    /**
     * Saves the given deadline to the database and returns it.
     * @param deadline deadline to be saved
     * @return saved deadline
     */
    public Deadline addDeadline(Deadline deadline) {
        return repository.save(deadline);
    }


    /**
     * Removes the deadline by the given id from the database if it exists
     * @param id of the deadline to remove
     */
    public void removeDeadline(Integer id) {
        Optional<Deadline> sOptional = repository.findById(id);

        if (sOptional.isPresent()) {
            Deadline deadline = sOptional.get();
            repository.deleteById(deadline.getId());
        }
    }

    /***
     * Function to get all deadlines in chronological order,
     * add colour for each deadline and save to repository
     *
     * @return deadline in chronological order
     */
    public List<Deadline> getAllDeadlinesOrderedWithColour() {
        List<Deadline> deadlines = getAllDeadlinesOrdered();
        ArrayList<String> colours = new ArrayList<>(
            Arrays.asList("#5897fc", "#a758fc", "#fc58c3", "#9e1212", "#c65102", "#d5b60a", "#004400", " #11887b"));
        int colIndex = 0;

        for (Deadline deadline : deadlines) {
            deadline.setDeadlineColour(colours.get(colIndex));
            repository.save(deadline);

            if (colIndex == (colours.size() - 1)) { // List max
                colIndex = 0;
            } else {
                colIndex++;
            }
        }
        return getAllDeadlinesOrdered();
    }

}
