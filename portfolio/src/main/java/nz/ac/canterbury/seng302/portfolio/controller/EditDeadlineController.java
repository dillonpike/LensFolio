package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller receive HTTP GET, POST, PUT, DELETE calls for edit deadline
 */
@Controller
public class EditDeadlineController {

    @Autowired
    private DeadlineService deadlineService;

    /**
     * Tries to save new data to deadline with given deadlineId to the database.
     * @param id Id of deadline edited
     * @param deadline Deadline data to be updated
     * @throws Exception if sprint cannot be found from the given ID or if it cannot be saved.
     */
    @PostMapping("/edit-deadline/{id}")
    public String deadlineEditSave(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal AuthState principal,
            @ModelAttribute("deadline") Deadline deadline,
            Model model
    ) throws Exception {
        Deadline newDeadline = deadlineService.getDeadlineById(id);
        newDeadline.setDeadlineName(deadline.getDeadlineName());
        newDeadline.setDeadlineDate(deadline.getDeadlineDate());

        deadlineService.updateDeadline(newDeadline);

        return "redirect:/details";
    }
}
