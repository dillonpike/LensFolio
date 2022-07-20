package nz.ac.canterbury.seng302.portfolio.service;

import java.util.ArrayList;
import java.util.Arrays;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
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

    @Autowired
    private DateValidationService dateValidationService;

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
     * @param sprints
     */
    public List<Deadline> getAllDeadlinesOrderedWithColour(List<Sprint> sprints) {
        List<Deadline> deadlinesList = getAllDeadlinesOrdered();
        for (Deadline currentDeadline : deadlinesList) {
            // Reset Deadline's color
            currentDeadline.setDeadlineColour("test");

            for (Sprint sprint : sprints) {
                if ((currentDeadline.getDeadlineDate().compareTo(sprint.getStartDate()) >= 0 && currentDeadline.getDeadlineDate().compareTo(sprint.getEndDate()) <= 0)) {
                    currentDeadline.setDeadlineColour(sprint.getColour());
                }
            }
            repository.save(currentDeadline);
        }
        return getAllDeadlinesOrdered();
    }

}
