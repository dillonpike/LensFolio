package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadlines;
import nz.ac.canterbury.seng302.portfolio.service.DeadlinesService;
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
    private DeadlinesService deadlinesService;

    /**
     * Tries to save new data to deadline with given deadlineId to the database.
     * @param id Id of deadline edited
     * @param deadlines Deadline data to be updated
     * @throws Exception if sprint cannot be found from the given ID or if it cannot be saved.
     */
    @PostMapping("/edit-deadline/{id}")
    public String deadlineEditSave(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal AuthState principal,
            @ModelAttribute("deadlines") Deadlines deadlines,
            Model model
    ) throws Exception {
        Deadlines newDeadline = deadlinesService.getDeadlineById(id);
        newDeadline.setDeadlineName(deadlines.getDeadlineName());
        newDeadline.setDeadlineDate(deadlines.getDeadlineDate());

        deadlinesService.updateDeadline(newDeadline);

        return "redirect:/details";
    }
}
