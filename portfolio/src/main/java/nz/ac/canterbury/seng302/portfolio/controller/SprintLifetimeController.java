package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Controller for the add sprint page
 */
@Controller
public class SprintLifetimeController {
    @Autowired
    private SprintService sprintService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DateValidationService dateValidationService;

    /**
     * Add a given number of days and/or weeks to a date.util using Calendars.
     *
     * @param toUpdate  The starting date.
     * @param day       The number of days to increase by.
     * @param week      The number of weeks to increase by.
     * @return          A new updated date.
     */
    public Date getUpdatedDate(Date toUpdate, int day, int week) {
        Calendar date = Calendar.getInstance();
        date.setTime(toUpdate);
        date.add(Calendar.DATE, day);
        date.add(Calendar.WEEK_OF_YEAR, week);
        return date.getTime();
    }

    /**
     * Gives the UI a blank sprint to use to add a new sprint.
     * @param model For adding the sprint and errors
     */
    @GetMapping("/add-sprint")
    public String sprintAddForm(Model model) {

        Sprint blankSprint = new Sprint();
        List<Sprint> sprints = sprintService.getAllSprintsOrdered();
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
            blankSprint.setName("Sprint " + (sprints.size() + 1));

            Sprint lastSprint = sprints.get(sprints.size() - 1);
            blankSprint.setStartDate(getUpdatedDate(lastSprint.getEndDate(), 1, 0));
            blankSprint.setEndDate(getUpdatedDate(lastSprint.getEndDate(), 0, 3));
        }


        model.addAttribute("sprint", blankSprint);
        model.addAttribute("sprintDateError", "");

        /* Return the name of the Thymeleaf template */
        return "addSprint";
    }

    /**
     * Tries to save the new sprint to the database
     * @param sprint New sprint
     */
    @PostMapping("/add-sprint")
    public String projectSave(
            @ModelAttribute("sprint") Sprint sprint,
            Model model
    ) {

        sprint.setStartDateString(sprint.getStartDateString());
        sprint.setEndDateString(sprint.getEndDateString());
        sprintService.addSprint(sprint);
        return "redirect:/details";
    }

    /**
     * Tries to delete a sprint with given id.
     * @param id Id of sprint being deleted
     * @throws Exception If deleting sprint does not work
     */
    @GetMapping("/delete-sprint/{id}")
    public String sprintRemove(@PathVariable("id") Integer id, Model model) throws Exception {

        sprintService.removeSprint(id);

        /* Return the name of the Thymeleaf template */
        return "redirect:/details";
    }

    @RequestMapping(value="/add-sprint/error", method=RequestMethod.POST)
    public String updateSprintRangeErrors(@RequestParam(value="sprintStartDate") String sprintStartDate,
                                          @RequestParam(value="sprintEndDate") String sprintEndDate,
                                          Model model) {
        model.addAttribute("sprintDateError",
                dateValidationService.validateStartDateNotAfterEndDate(sprintStartDate, sprintEndDate) + " " +
                        dateValidationService.validateSprintDateRange(sprintStartDate, sprintEndDate, -1) + " " +
                        dateValidationService.validateSprintInProjectDateRange(sprintStartDate, sprintEndDate));
        return "addSprint :: #sprintDateError";
    }
}
