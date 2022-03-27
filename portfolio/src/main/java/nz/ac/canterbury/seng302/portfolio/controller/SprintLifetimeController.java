package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for the add sprint page
 */
@Controller
public class SprintLifetimeController {
    @Autowired
    private SprintService sprintService;

    @GetMapping("/add-sprint")
    public String sprintAddForm(Model model) throws Exception {

        Sprint blankSprint = new Sprint();
        model.addAttribute("sprint", blankSprint);

        /* Return the name of the Thymeleaf template */
        return "addSprint";
    }

    @PostMapping("/add-sprint")
    public String projectSave(
            @ModelAttribute("sprint") Sprint sprint,
            Model model
    ) throws Exception {
        sprintService.addSprint(sprint);
        return "redirect:/details";
    }

    @GetMapping("/delete-sprint/{id}")
    public String sprintRemove(@PathVariable("id") Integer id, Model model) throws Exception {

        sprintService.removeSprint(id);

        /* Return the name of the Thymeleaf template */
        return "redirect:/details";
    }
}
