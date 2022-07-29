package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import nz.ac.canterbury.seng302.portfolio.service.PermissionService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EditMilestoneController {

    @Autowired
    MilestoneService milestoneService;

    @Autowired
    private PermissionService permissionService;

    /**
     * Save an edited milestone.
     * @param id ID of milestone being saved
     * @param milestone Milestone with changes being saved
     * @param model DOM model
     * @return redirect link
     * @throws Exception Thrown if milestone with given ID does not exist.
     */
    @PostMapping("edit-milestone/{id}")
    public String saveEditedMilestone(
            @PathVariable("id") Integer id,
            @ModelAttribute("milestone") Milestone milestone,
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) throws Exception {
        if (permissionService.isValid(principal, model)) {
            Milestone newMilestone = milestoneService.getMilestoneById(id);
            newMilestone.setMilestoneName(milestone.getMilestoneName());
            newMilestone.setMilestoneDate(milestone.getMilestoneDate());

            milestoneService.updateMilestone(newMilestone);
        }


        return "redirect:/details";
    }
}
