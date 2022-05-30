package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

/***
 * Contains methods for saving, deleting, updating and retrieving sprint objects to the database.
 */
@Service
public class SprintService {

    @Autowired
    private SprintRepository repository;

    /**
     * Get list of all sprints
     * @return List of sprints
     */
    public List<Sprint> getAllSprints() {
        return (List<Sprint>) repository.findAll();
    }

    /**
     * Get sprint by Id
     * @param id id of sprint
     * @return Sprint with the id that is the input
     * @throws Exception If sprint can't be found
     */
    public Sprint getSprintById(Integer id) throws Exception {

        Optional<Sprint> sprint = repository.findById(id);
        if(sprint.isPresent()) {
            return sprint.get();
        } else {
            throw new Exception("Sprint not found");
        }
    }

    /**
     * Updates a sprint
     * @param sprint Sprint to update it to
     * @return Newly edited sprint
     */
    public Sprint updateSprint(Sprint sprint) {
        Optional<Sprint> sOptional = repository.findById((Integer) sprint.getId());

        if (sOptional.isPresent()) {
            Sprint sprintUpdate = sOptional.get();
            sprintUpdate.setDescription(sprint.getDescription());
            sprintUpdate.setStartDate(sprint.getStartDate());
            sprintUpdate.setEndDate(sprint.getEndDate());
            sprintUpdate.setName(sprint.getName());

            sprintUpdate = repository.save(sprintUpdate);
            return sprintUpdate;
        } else {
            sprint = repository.save(sprint);
            return sprint;
        }
    }

    /**
     * Add a new sprint to the database. It gives the new sprint an ID based on sprintIdCount.
     * @param sprint New sprint to add
     * @return Sprint that was added to the database
     */
    public Sprint addSprint(Sprint sprint) {
        sprint = repository.save(sprint);
        return sprint;
    }

    /**
     * Remove a sprint from the database.
     * @param id Id of the sprint being removed
     */
    public void removeSprint(Integer id) {
        Optional<Sprint> sOptional = repository.findById(id);

        if(sOptional.isPresent()) {
            Sprint sprintUpdate = sOptional.get();
            repository.deleteById(sprintUpdate.getId());
        }
    }

    /**
     * Get list of all sprints
     * @return List of sprints
     */
    public List<Sprint> getAllSprintsOrdered() {
        return repository.findAllByOrderBySprintStartDate();
    }
}
