package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for the edit sprint details page
 */
@Controller
public class EditSprintController {
    @Autowired
    private SprintService sprintService;

    @GetMapping("/edit-sprint/{id}")
    public String sprintForm(@PathVariable("id") Integer id, Model model ) throws Exception {
        Sprint sprint = sprintService.getSprintById(id);
        /* Add sprint details to the model */
        model.addAttribute("sprintId", id);
        model.addAttribute("sprintName", sprint.getName());
        model.addAttribute("sprintStartDate", sprint.getStartDateString());
        model.addAttribute("sprintEndDate", sprint.getEndDateString());
        model.addAttribute("sprintDescription", sprint.getDescription());

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

}
