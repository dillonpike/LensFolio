package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.PermissionService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller receive HTTP GET, POST, PUT, DELETE calls for edit deadline
 */
@Controller
public class EditDeadlineController {

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private PermissionService permissionService;

    /**
     * Tries to save new data to deadline with given deadlineId to the database.
     * @param id Id of deadline edited
     * @param deadline Deadline data to be updated
     * @throws Exception if sprint cannot be found from the given ID or if it cannot be saved.
     */
    @PostMapping("/edit-deadline/{id}")
    public String deadlineEditSave(
            @PathVariable("id") Integer id,
            RedirectAttributes rm,
            @ModelAttribute("deadline") Deadline deadline,
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) throws Exception {
        if (permissionService.isValid(principal, model)) {
            Deadline newDeadline = deadlineService.getDeadlineById(id);
            newDeadline.setDeadlineName(deadline.getDeadlineName());
            newDeadline.setDeadlineDate(deadline.getDeadlineDate());
            deadlineService.updateDeadline(newDeadline);
        } else{
            rm.addFlashAttribute("isAccessDenied", true);
        }
        return "redirect:/details";

    }
}
