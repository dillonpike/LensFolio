package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

/**
 * Controller that handles adding and deleting milestone.
 */
@Controller
public class MilestoneLifetimeController {
    /**
     * Service for persisting milestone.
     */
    @Autowired
    private MilestoneService milestoneService;

    /**
     * Saves the given milestone to the database and redirects the user to the details page.
     * @param milestone new milestone to be saved
     */
    @PostMapping("/add-milestone")
    public String milestoneSave(@ModelAttribute("milestone") Milestone milestone, Model model) {
        milestoneService.addMilestone(milestone);
        return "redirect:/details";
    }

    /**
     * Tries to delete a milestone with given id.
     * @param id id of milestone being deleted
     */
    @GetMapping("/delete-milestone/{id}")
    public String milestoneRemove(@PathVariable("id") Integer id) {
        milestoneService.removeMilestone(id);
        return "redirect:/details";
    }
}
