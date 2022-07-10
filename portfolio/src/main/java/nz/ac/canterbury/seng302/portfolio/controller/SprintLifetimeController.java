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

    /**
     * Add a given number of days and/or weeks to a date.util using Calendars.
     *
     * @param toUpdate  The starting date.
     * @param day       The number of days to increase by.
     * @param week      The number of weeks to increase by.
     * @return          A new updated date.
     */
    public static Date getUpdatedDate(Date toUpdate, int day, int week) {
        Calendar date = Calendar.getInstance();
        date.setTime(toUpdate);
        date.add(Calendar.DATE, day);
        date.add(Calendar.WEEK_OF_YEAR, week);
        return date.getTime();
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
}
