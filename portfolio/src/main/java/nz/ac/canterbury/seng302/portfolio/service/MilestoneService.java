package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/***
 * Service class for saving, deleting, updating and retrieving milestone objects to the database.
 */
@Service
public class MilestoneService {
    @Autowired
    private MilestoneRepository repository;

    /**
     * Saves the given milestone to the database and returns it.
     * @param milestone milestone to be saved
     * @return saved milestone
     */
    public Milestone addMilestone(Milestone milestone) {
        return repository.save(milestone);
    }

}
