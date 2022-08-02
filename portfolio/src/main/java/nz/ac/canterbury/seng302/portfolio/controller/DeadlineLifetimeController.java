package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.PermissionService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller that handles adding and deleting deadlines.
 */
@Controller
public class DeadlineLifetimeController {

    /**
     * Service for persisting deadlines.
     */
    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    /**
     * Saves the given deadline to the database and redirects the user to the details page.
     * @param deadline new deadline to be saved
     */
    @PostMapping("/add-deadline")
    public String deadlineSave(@ModelAttribute("deadline") Deadline deadline,
                               @AuthenticationPrincipal AuthState principal,
                               Model model
    ) {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);

        if (permissionService.isValidToModifyProjectPage(userID)) {
            deadlineService.addDeadline(deadline);
        }
        return "redirect:/details";
    }

    /**
     * Tried to delete a deadline with given id
     * @param id of the deadline being deleted
     */
    @GetMapping("/delete-deadline/{id}")
    public String deadlineRemove(@PathVariable("id") Integer id,
                                 @AuthenticationPrincipal AuthState principal,
                                 Model model) {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);

        if (permissionService.isValidToModifyProjectPage(userID)) {
            deadlineService.removeDeadline(id);
        }
        return "redirect:/details";
    }
}
