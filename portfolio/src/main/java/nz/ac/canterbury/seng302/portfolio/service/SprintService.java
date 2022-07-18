package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.portfolio.controller.SprintLifetimeController.getUpdatedDate;


// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

/***
 * Contains methods for saving, deleting, updating and retrieving sprint objects to the database.
 */
@Service
public class SprintService {

    @Autowired
    private SprintRepository repository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EventService eventService;

    @Autowired
    private DateValidationService dateValidationService;

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

        if (sOptional.isPresent()) {
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


    /**
     * Updates the sprint identified by the given id with the given dates.
     * Returns true if update is successful, otherwise false.
     * @param id id of sprint to update
     * @param sprintStartDate new start date
     * @param sprintEndDate new end date
     * @return true if update is successful, otherwise false
     */
    public boolean updateSprintDates(Integer id, String sprintStartDate, String sprintEndDate) {
        Optional<Sprint> sOptional = repository.findById(id);

        if (sOptional.isPresent()) {
            Sprint sprintUpdate = sOptional.get();
            sprintUpdate.setStartDate(calendarDateStringToDate(sprintStartDate, false));
            sprintUpdate.setEndDate(calendarDateStringToDate(sprintEndDate, true));
            repository.save(sprintUpdate);
            return true;
        }
        return false;
    }

    /**
     * Converts a date string from the calendar to a date object.
     *
     * @param dateString the string to read as a date in format 2000/12/30
     * @return the given date, as a date object
     */
    public Date calendarDateStringToDate(String dateString, boolean isEndDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            if (isEndDate) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.MINUTE, -1);
                date = calendar.getTime();
            }
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        return date;
    }

    /**
     * Returns a suggested sprint for the user to create.
     * @return suggested sprint for the user to create
     */
    public Sprint getSuggestedSprint() {
        Sprint blankSprint = new Sprint();
        List<Sprint> sprints = getAllSprintsOrdered();
        if (sprints.isEmpty()) {
            blankSprint.setName("Sprint 1");
            try {
                Project project = projectService.getProjectById(0);
                blankSprint.setStartDate(project.getStartDate());
                blankSprint.setEndDate(getUpdatedDate(project.getStartDate(), 0, 3));
            } catch (Exception e) {
                Date now = Date.from(Instant.from(LocalDate.now()));
                blankSprint.setStartDate(now);
                blankSprint.setEndDate(getUpdatedDate(now, 0, 3));
            }
        } else {
            Project project;
            try {
                project = projectService.getProjectById(0);
            } catch (Exception e) {
                int id = sprints.get(sprints.size() - 1).getParentProjectId();
                try {
                    project = projectService.getProjectById(id);
                } catch (Exception e2) {
                    return new Sprint();
                }
            }

            blankSprint.setName("Sprint " + (sprints.size() + 1));
            Sprint lastSprint = sprints.get(sprints.size() - 1);
            if (!getUpdatedDate(lastSprint.getEndDate(), 1, 0).after(project.getEndDate())) { // not at end of project
                blankSprint.setStartDate(getUpdatedDate(lastSprint.getEndDate(), 1, 0));
                if (getUpdatedDate(lastSprint.getEndDate(), 0, 3).after(project.getEndDate())) { // sprint end not at end of project
                    blankSprint.setEndDate(project.getEndDate());
                } else {
                    blankSprint.setEndDate(getUpdatedDate(lastSprint.getEndDate(), 0, 3));
                }
            } else {
                blankSprint.setStartDate(lastSprint.getEndDate());
                blankSprint.setEndDate(lastSprint.getEndDate());
            }
        }
        return blankSprint;
    }

    /***
     * Function to get all sprints in chronological order,
     * add colour for each sprint and save to repository
     *
     * @return sprints in chronological order
     */
    public List<Sprint> getAllSprintsOrderedWithColour() {
        List<Sprint> sprints = getAllSprintsOrdered();
        ArrayList<String> colours = new ArrayList<>(Arrays.asList("#5897fc", "#a758fc", "#fc58c3", "#9e1212", "#c65102", "#d5b60a", "#004400", " #11887b"));
        int colIndex = 0;

        for (Sprint sprint : sprints) {
            sprint.setColour(colours.get(colIndex));
            repository.save(sprint);

            if (colIndex == (colours.size() - 1)) { // List max
                colIndex = 0;
            } else {
                colIndex++;
            }
        }
        return getAllSprintsOrdered();
    }

    /**
     * Gets a list of events that overlap with the given sprint in some way. This is to know what events should be
     * displayed with this sprint. It does this by checking if either of the dates are within the sprints dates.
     * @param sprint Sprint to check events against.
     * @return List of events that overlap with the given sprint.
     */
    public List<Event> getAllEventsOverlappingWithSprint(Sprint sprint) {
        ArrayList<Event> eventsList = (ArrayList<Event>) eventService.getAllEventsOrdered();
        ArrayList<Event> eventsOverlapped = new ArrayList<>();

        for (Event currentEvent : eventsList) {
            if (dateValidationService.validateEventStartDateInSprintDate(currentEvent, sprint) ||
                    dateValidationService.validateEventEndDateInSprintDate(currentEvent, sprint) ||
                    // For events that start before and go after the sprint (would not be present with above checks).
                    (currentEvent.getEventStartDate().before(sprint.getStartDate()) && currentEvent.getEventEndDate().after(sprint.getEndDate()))
            ) {
                eventsOverlapped.add(currentEvent);
            }
        }
        return eventsOverlapped;
    }
}
