package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public class EditGroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private DateValidationService dateValidationService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private RegisterClientService registerClientService;


    /**
     * Tries to save new data to group with given groupId to the database.
     * @param id Id of event edited
     * @param group Group data to be updated
     * @throws ObjectNotFoundException if group cannot be found from the given ID or if it cannot be saved.
     */
    @PostMapping("/edit-group/{id}")
    public String groupEditsave(
        @PathVariable("id") Integer id,
        @AuthenticationPrincipal AuthState principal,
        @ModelAttribute("group") Group group,
        Model model
    ) throws ObjectNotFoundException {
      groupService.editGroupDetails(group.getGroupId(), group.getShortName(), group.getLongName());
      return "redirect:/groups";
    }
}
