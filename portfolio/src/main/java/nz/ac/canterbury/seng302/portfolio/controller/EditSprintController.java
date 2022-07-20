package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


/**
 * Controller for the edit sprint details page
 */
@Controller
public class EditSprintController {

    @Autowired
    private SprintService sprintService;

    @Autowired
    private DateValidationService dateValidationService;

    /**
     * Gets data for editing a given sprint.
     * @param id Id of sprint
     * @param model Used to display the sprint data to the UI
     * @throws Exception If getting the sprint from the given id fails
     */
    @GetMapping("/edit-sprint/{id}")
    public String sprintForm(@PathVariable("id") Integer id, Model model ) throws Exception {
        Sprint sprint = sprintService.getSprintById(id);
        /* Add sprint details to the model */
        model.addAttribute("sprint", sprint);
        model.addAttribute("sprintId", id);
        model.addAttribute("sprintDateError", "");

        /* Return the name of the Thymeleaf template */
        return "editSprint";
    }

    /**
     * Tries to save new data to sprint with given sprintId to the database.
     * @param id Id of sprint edited
     * @param sprint New sprint object
     * @throws Exception if sprint cannot be found from the given ID or if it cannot be saved.
     */
    @PostMapping("/edit-sprint/{id}")
    public String sprintSave(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal AuthState principal,
            @ModelAttribute("sprint") Sprint sprint,
            Model model
    ) throws Exception {
        // Gets the project with id 0 to plonk on the page
        Sprint newSprint = sprintService.getSprintById(id);
        newSprint.setName(sprint.getName());
        newSprint.setStartDateString(sprint.getStartDateString());
        newSprint.setEndDateString(sprint.getEndDateString());
        newSprint.setDescription(sprint.getDescription());

        sprintService.updateSprint(newSprint);

        return "redirect:/details";
    }

    @RequestMapping(value="/edit-sprint/error", method=RequestMethod.POST)
    public String updateSprintRangeErrors(@RequestParam(value="id") Integer id,
                                          @RequestParam(value="sprintStartDate") String sprintStartDate,
                                          @RequestParam(value="sprintEndDate") String sprintEndDate,
                                          Model model) {
        model.addAttribute("sprintDateError",
                dateValidationService.validateDateRangeNotEmpty(sprintStartDate, sprintEndDate) + " " +
                dateValidationService.validateStartDateNotAfterEndDate(sprintStartDate, sprintEndDate) + " " +
                dateValidationService.validateSprintDateRange(sprintStartDate, sprintEndDate, id) + " " +
                dateValidationService.validateDatesInProjectDateRange(sprintStartDate, sprintEndDate));
        return "editSprint :: #sprintDateError";
    }
}
