package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import nz.ac.canterbury.seng302.portfolio.service.PermissionService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.ws.rs.NotAcceptableException;


/**
 * Controller receive HTTP GET, POST, PUT, DELETE calls for edit milestone
 */
@Controller
public class EditMilestoneController {

    private static final Logger logger = LoggerFactory.getLogger(EditMilestoneController.class);

    @Autowired
    MilestoneService milestoneService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    /**
     * Save an edited milestone.
     * @param id ID of milestone being saved
     * @param milestone Milestone with changes being saved
     * @param model DOM model
     * @return redirect link
     * @throws ObjectNotFoundException Thrown if milestone with given ID does not exist.
     */
    @PostMapping("edit-milestone/{id}")
    public String saveEditedMilestone(
            @PathVariable("id") Integer id,
            @ModelAttribute("milestone") Milestone milestone,
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) throws ObjectNotFoundException {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);

        elementService.addHeaderAttributes(model, userID);
        try {
            milestoneService.validateMilestone(milestone, model);
            if (permissionService.isValidToModify(userID)) {
                Milestone newMilestone = milestoneService.getMilestoneById(id);
                newMilestone.setMilestoneName(milestone.getMilestoneName());
                newMilestone.setMilestoneDate(milestone.getMilestoneDate());

                milestoneService.updateMilestone(newMilestone);
            }
        } catch (NotAcceptableException e) {
            logger.error(String.format("Error saving milestone with id %d: %s", id, e.getMessage()));
        }


        return "redirect:/details";
    }
}
