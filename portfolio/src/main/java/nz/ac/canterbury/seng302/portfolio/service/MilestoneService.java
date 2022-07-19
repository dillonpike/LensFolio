package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import java.util.List;

/***
 * Service class for saving, deleting, updating and retrieving milestone objects to the database.
 */
@Service
public class MilestoneService {
    @Autowired
    private MilestoneRepository repository;

    /**
     * Get list of all milestones
     * @return List of milestones
     */
    public List<Milestone> getAllMilestones() {
        return (List<Milestone>) repository.findAll();
    }

    /**
     * Get list of all milestones ordered
     * @return List of ordered milestones
     */
    public List<Milestone> getAllMilestonesOrdered() {
        return repository.findAllByOrderByMilestoneDate();
    }

    /**
     * Saves the given milestone to the database and returns it.
     * @param milestone milestone to be saved
     * @return saved milestone
     */
    public Milestone addMilestone(Milestone milestone) {
        return repository.save(milestone);
    }

    /**
     * Removes the milestone identified by the given id from the database if it exists.
     * @param id id of the milestone to remove
     */
    public void removeMilestone(Integer id) {
        Optional<Milestone> sOptional = repository.findById(id);

        if (sOptional.isPresent()) {
            Milestone milestone = sOptional.get();
            repository.deleteById(milestone.getId());
        }
    }

    /**
     * Get milestone by given ID from repository.
     * @param id Id of milestone requested.
     * @return Milestone object from repository.
     * @throws Exception Thrown if no milestone exists.
     */
    public Milestone getMilestoneById(Integer id) throws Exception {
        Optional<Milestone> milestone = repository.findById(id);

        if (milestone.isPresent()) {
            return milestone.get();
        } else {
            throw new Exception("Milestone not found");
        }
    }

    /**
     * Updates a milestone in the repository.
     * @param milestone Edited milestone to save.
     * @return Saved milestone as it appears in the repository.
     */
    public Milestone updateMilestone(Milestone milestone) {
        Optional<Milestone> mOptional = repository.findById((Integer) milestone.getId());

        if (mOptional.isPresent()) {
            Milestone milestoneUpdate = mOptional.get();
            milestoneUpdate.setMilestoneName(milestone.getMilestoneName());
            milestoneUpdate.setMilestoneDate(milestone.getMilestoneDate());

            milestoneUpdate = repository.save(milestoneUpdate);
            return milestoneUpdate;
        } else {
            milestone = repository.save(milestone);
            return milestone;
        }
    }

    /**
     * Gets a list of milestones that overlap with the given sprint in some way. This is to know what milestones should be
     * displayed with this sprint. It does this by checking if the date is within the sprints dates.
     * @param sprint Sprint to check milestones against.
     * @return List of milestones that are within the given sprint.
     */
    public List<Milestone> getAllMilestonesOverlappingWithSprint(Sprint sprint) {
        ArrayList<Milestone> milestonesList = (ArrayList<Milestone>) getAllMilestonesOrdered();
        ArrayList<Milestone> milestonesOverlapped = new ArrayList<>();

        for (Milestone currentMilestone : milestonesList) {
            if (currentMilestone.getMilestoneDate().equals(sprint.getStartDate()) ||
                    currentMilestone.getMilestoneDate().equals(sprint.getEndDate()) ||
                    (currentMilestone.getMilestoneDate().after(sprint.getStartDate()) &&
                            currentMilestone.getMilestoneDate().before(sprint.getEndDate())
                    )
            ) {
                milestonesOverlapped.add(currentMilestone);
            }
        }
        return milestonesOverlapped;
    }
}
