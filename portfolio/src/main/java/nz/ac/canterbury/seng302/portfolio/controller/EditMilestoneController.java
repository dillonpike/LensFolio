package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
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


    @PostMapping("edit-milestone/{id}")
    public String saveEditedMilestone(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal AuthState principal,
            @ModelAttribute("milestone") Milestone milestone,
            Model model
    ) throws Exception {
        Milestone newMilestone = milestoneService.getMilestoneById(id);
        newMilestone.setMilestoneName(milestone.getMilestoneName());
        newMilestone.setMilestoneDate(milestone.getMilestoneDate());

        milestoneService.updateMilestone(newMilestone);

        return "redirect:/details";
    }
}
