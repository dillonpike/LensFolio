package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.*;
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

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

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
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);
        if (permissionService.isValidToModifyProjectPage(userID)) {
            // Gets the project with id 0 to plonk on the page
            Sprint newSprint = sprintService.getSprintById(id);
            newSprint.setName(sprint.getName());
            newSprint.setStartDateString(sprint.getStartDateString());
            newSprint.setEndDateString(sprint.getEndDateString());
            newSprint.setDescription(sprint.getDescription());

            sprintService.updateSprint(newSprint);
        }


        return "redirect:/details";
    }
}
