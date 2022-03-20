package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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

    @GetMapping("/edit-sprint/{id}")
    public String sprintForm(@PathVariable("id") Integer id, Model model ) throws Exception {
        Sprint sprint = sprintService.getSprintById(id);
        /* Add sprint details to the model */
        model.addAttribute("sprintId", id);
        model.addAttribute("sprintName", sprint.getName());
        model.addAttribute("sprintStartDate", sprint.getStartDateString());
        model.addAttribute("sprintEndDate", sprint.getEndDateString());
        model.addAttribute("sprintDescription", sprint.getDescription());
        model.addAttribute("sprintStartDateError", "");
        model.addAttribute("sprintDateError", "");

        /* Return the name of the Thymeleaf template */
        return "editSprint";
    }

    @PostMapping("/edit-sprint/{id}")
    public String sprintSave(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="sprintName") String sprintName,
            @RequestParam(value="sprintStartDate") String sprintStartDate,
            @RequestParam(value="sprintEndDate") String sprintEndDate,
            @RequestParam(value="sprintDescription") String sprintDescription,
            Model model
    ) throws Exception {
        // Gets the project with id 0 to plonk on the page
        Sprint newSprint = sprintService.getSprintById(id);
        newSprint.setName(sprintName);
        newSprint.setStartDateString(sprintStartDate);
        newSprint.setEndDateString(sprintEndDate);
        newSprint.setDescription(sprintDescription);

        sprintService.updateSprint(newSprint);

        return "redirect:/edit-sprint/"+id;
    }

    @RequestMapping(value="/edit-sprint/error", method=RequestMethod.POST)
    public String updateSprintRangeErrors(@RequestParam(value="id") Integer id,
                                     @RequestParam(value="sprintStartDate") String sprintStartDate,
                                     @RequestParam(value="sprintEndDate") String sprintEndDate,
                                     Model model) {
        model.addAttribute("sprintDateError",
                dateValidationService.validateSprintStartDate(sprintStartDate, sprintEndDate) + " " +
                dateValidationService.validateSprintDateRange(sprintStartDate, sprintEndDate, id));
        return "editSprint :: #sprintDateError";
    }


}
