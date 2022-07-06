package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
}
