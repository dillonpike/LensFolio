package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAcceptableException;

/**
 * Controller receive HTTP GET, POST, PUT, DELETE calls for edit deadline
 */
@Controller
public class EditDeadlineController {

    private static final Logger logger = LoggerFactory.getLogger(EditDeadlineController.class);

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    /**
     * Tries to save new data to deadline with given deadlineId to the database.
     * @param id Id of deadline edited
     * @param deadline Deadline data to be updated
     * @throws ObjectNotFoundException if deadline cannot be found from the given ID or if it cannot be saved.
     */
    @PostMapping("/edit-deadline/{id}")
    public String deadlineEditSave(
            @PathVariable("id") Integer id,
            RedirectAttributes rm,
            @ModelAttribute("deadline") Deadline deadline,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            HttpServletResponse httpServletResponse
    ) throws ObjectNotFoundException {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);

        try {
            deadlineService.validateDeadline(deadline, model);
            if (permissionService.isValidToModify(userID)) {
                Deadline newDeadline = deadlineService.getDeadlineById(id);
                newDeadline.setDeadlineName(deadline.getDeadlineName());
                newDeadline.setDeadlineDate(deadline.getDeadlineDate());
                deadlineService.updateDeadline(newDeadline);
            } else {
                rm.addFlashAttribute("isAccessDenied", true);
            }
        } catch (NotAcceptableException e) {
            logger.error(String.format("Error while updating deadline with id  %d: %s", id, e.getMessage()));
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "fragments/deadlineModal::deadlineModalBody";
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        return "fragments/deadlineModal::deadlineModalBody";
    }
}
