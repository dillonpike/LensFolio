package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.PermissionService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAcceptableException;

/**
 * Controller that handles adding and deleting deadlines.
 */
@Controller
public class DeadlineLifetimeController {

    private static final Logger logger = LoggerFactory.getLogger(DeadlineLifetimeController.class);

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
    public String deadlineSave(
            @ModelAttribute("deadline") Deadline deadline,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            HttpServletResponse httpServletResponse
    ) {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);

        try {
            deadlineService.validateDeadline(deadline, model);
            if (permissionService.isValidToModify(userID)) {
                deadlineService.addDeadline(deadline);
            }
        } catch (NotAcceptableException e) {
            logger.error(String.format("Error adding deadline: %s", e.getMessage()));
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "fragments/deadlineModal::deadlineModalBody";
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        return "fragments/deadlineModal::deadlineModalBody";
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

        if (permissionService.isValidToModify(userID)) {
            deadlineService.removeDeadline(id);
        }
        return "redirect:/details";
    }
}
