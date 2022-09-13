package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.ws.rs.NotAcceptableException;
import java.util.Date;
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

    private static final String MILESTONE_NAME_ERROR_MESSAGE = "milestoneAlertMessage";

    private static final String MILESTONE_DATE_ERROR_MESSAGE = "milestoneDateAlertMessage";


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
     * Saves the given milestone to the database after removing whitespace from name
     * and returns it.
     * @param milestone milestone to be saved
     * @return saved milestone
     */
    public Milestone addMilestone(Milestone milestone) {
        String milestoneName = milestone.getMilestoneName().trim();
        milestone.setMilestoneName(milestoneName);
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
    public Milestone getMilestoneById(Integer id) throws ObjectNotFoundException {
        Optional<Milestone> milestone = repository.findById(id);

        if (milestone.isPresent()) {
            return milestone.get();
        } else {
            throw new ObjectNotFoundException(id, "Unknown Milestone");
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
            milestoneUpdate.setMilestoneName(milestone.getMilestoneName().trim());
            milestoneUpdate.setMilestoneDate(milestone.getMilestoneDate());

            milestoneUpdate = repository.save(milestoneUpdate);
            return milestoneUpdate;
        } else {
            milestone = repository.save(milestone);
            return milestone;
        }
    }

    /***
     * For any events existing, get the sprints colour for its start date if it is within the sprint time slot,
     * and the same is done with the events end date
     *
     * @param sprints sprints in chronological order
     * @return events in chronological order
     */
    public List<Milestone> getAllEventsOrderedWithColour(List<Sprint> sprints) {
        List<Milestone> milestoneList = getAllMilestonesOrdered();
        for (Milestone currentMilestone : milestoneList) {
            // Reset Event's color
            currentMilestone.setColour(null);

            for (Sprint sprint : sprints) {
                if (validateMilestoneDateInSprintDateRange(currentMilestone, sprint)) {
                    currentMilestone.setColour(sprint.getColour());
                    break;
                }
            }
            repository.save(currentMilestone);
        }
        return getAllMilestonesOrdered();
    }

    /**
     * Validate if particular milestone date is in sprint date range
     * @param milestone The update milestone
     * @param sprint The sprint to compare with
     * @return True if milestone end date is in sprint date range
     */
    public boolean validateMilestoneDateInSprintDateRange(Milestone milestone, Sprint sprint) {
        Date milestoneDate = milestone.getMilestoneDate();
        Date sprintStartDate = sprint.getStartDate();
        Date sprintEndDate = sprint.getEndDate();
        return sprintStartDate.compareTo(milestoneDate) * sprintEndDate.compareTo(milestoneDate) <= 0;
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

    /**
     * Validate a milestones fields to ensure they are valid.
     * @param milestone Milestone to validate
     * @param model Model to add errors to
     * @throws NotAcceptableException If the milestone is not valid
     */
    public void validateMilestone(Milestone milestone, Model model) throws NotAcceptableException {

        milestone.setMilestoneName(milestone.getMilestoneName().trim());
        boolean hasError = false;
        if (milestone.getMilestoneName() == null || milestone.getMilestoneName().trim().isEmpty()) {
            model.addAttribute(MILESTONE_NAME_ERROR_MESSAGE, "Milestone name cannot be empty");
            hasError = true;
        } else if (milestone.getMilestoneName().length() < 2) {
            model.addAttribute(MILESTONE_NAME_ERROR_MESSAGE, "Name must be at least 2 characters");
            hasError = true;
        } else if (milestone.getMilestoneName().length() > 30) {
            model.addAttribute(MILESTONE_NAME_ERROR_MESSAGE, "Name cannot be greater than 30 characters");
            hasError = true;
        }
        if (milestone.getMilestoneDate() == null || milestone.getMilestoneDate().before(new Date(0)) || milestone.getMilestoneDate().equals(new Date(0))) {
            model.addAttribute(MILESTONE_DATE_ERROR_MESSAGE, "Correctly formatted date is required");
            hasError = true;
        }
        if (hasError) {
            throw new NotAcceptableException("Milestone fields have errors");
        }
    }
}
