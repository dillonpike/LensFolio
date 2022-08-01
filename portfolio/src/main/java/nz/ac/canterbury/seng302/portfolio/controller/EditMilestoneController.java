package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EditMilestoneController {

    @Autowired
    MilestoneService milestoneService;

    /**
     * Save an edited milestone.
     * @param id ID of milestone being saved
     * @param milestone Milestone with changes being saved
     * @return redirect link
     * @throws Exception Thrown if milestone with given ID does not exist.
     */
    @PostMapping("/edit-milestone/{id}")
    public String saveEditedMilestone(
            @PathVariable("id") Integer id,
            @ModelAttribute("milestone") Milestone milestone
    ) throws Exception {
        Milestone newMilestone = milestoneService.getMilestoneById(id);
        newMilestone.setMilestoneName(milestone.getMilestoneName());
        newMilestone.setMilestoneDate(milestone.getMilestoneDate());

        milestoneService.updateMilestone(newMilestone);

        return "redirect:/details";
    }
}
