package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Controller for the add sprint page
 */
@Controller
public class SprintLifetimeController {
    @Autowired
    private SprintService sprintService;

    @Autowired
    private DateValidationService dateValidationService;

    /**
     * Gives the UI a blank sprint to use to add a new sprint.
     * @param model For adding the sprint and errors
     */
    @GetMapping("/add-sprint")
    public String sprintAddForm(Model model) {

        Sprint blankSprint = new Sprint();
        List<Sprint> sprints = sprintService.getAllSprintsOrdered();
        if (sprints.isEmpty()) {
            blankSprint.setName("Sprint 1"); //TODO have the code use project as default dates.
            blankSprint.setStartDate(java.sql.Date.valueOf(LocalDate.now()));
            blankSprint.setStartDate(java.sql.Date.valueOf(LocalDate.now().plusWeeks(3)));
        } else {
            blankSprint.setName("Sprint " + (sprints.size() + 1));

            Sprint lastSprint = sprints.get(sprints.size() - 1);
            LocalDate start_date = lastSprint.getEndDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            blankSprint.setStartDate(java.sql.Date.valueOf(start_date.plusDays(1)));
            blankSprint.setEndDate(java.sql.Date.valueOf(start_date.plusDays(1).plusWeeks((3))));
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
