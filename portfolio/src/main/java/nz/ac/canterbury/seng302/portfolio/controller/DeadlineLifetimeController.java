package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller that handles adding and deleting deadlines.
 */
public class DeadlineLifetimeController {

    /**
     * Service for persisting deadlines.
     */
    @Autowired
    private DeadlineService deadlineService;

    /**
     * Saves the given deadline to the database and redirects the user to the details page.
     * @param deadline new deadline to be saved
     */
    @PostMapping("/add-deadline")
    public String projectSave(@ModelAttribute("deadline") Deadline deadline) {
        deadlineService.addDeadline(deadline);
        return "redirect:/details";
    }
}
