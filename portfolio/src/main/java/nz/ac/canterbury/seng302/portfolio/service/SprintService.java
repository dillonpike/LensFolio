package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class SprintService {
    @Autowired
    private SprintRepository repository;

    /**
     * Get list of all sprints
     */
    public List<Sprint> getAllSprints() {
        List<Sprint> list = (List<Sprint>) repository.findAll();
        return list;
    }

    /**
     * Get sprint by id
     */
    public Sprint getSprintById(Integer id) throws Exception {

        Optional<Sprint> sprint = repository.findById(id);
        if(sprint!=null) {
            return sprint.get();
        }
        else
        {
            throw new Exception("Project not found");
        }
    }

    /**
     *
     * updates a sprint.
     */
    public Sprint updateSprint(Sprint sprint) {
        Optional<Sprint> sOptional = repository.findById((Integer) sprint.getId());

        if(sOptional != null) {
            Sprint sprintUpdate = sOptional.get();
            sprintUpdate.setDescription(sprint.getDescription());
            sprintUpdate.setStartDate(sprint.getStartDate());
            sprintUpdate.setEndDate(sprint.getEndDate());
            sprintUpdate.setName(sprint.getName());

            sprintUpdate = repository.save(sprintUpdate);
            return sprintUpdate;
        }
        else {
            sprint = repository.save(sprint);
            return sprint;
        }
    }

    public Sprint addSprint(Sprint sprint) {
        sprint = repository.save(sprint);
        return sprint;
    }

    public void removeSprint(Integer id) {
        Optional<Sprint> sOptional = repository.findById(id);

        if(sOptional.isPresent()) {
            Sprint sprintUpdate = sOptional.get();
            repository.delete(sprintUpdate);
        }
    }

}
