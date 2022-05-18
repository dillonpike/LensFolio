package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for the add event page
 */
@Controller
public class EventLifetimeController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DateValidationService dateValidationService;

    /**
     * Navigates to the add event page and saves new event
     * @param model For adding the event and error handling
     */
    @GetMapping("add-event")
    public String eventAddForm(Model model) throws Exception {

        return "addEvent";

    }

    /**
     * Tries to save the new event to the database
     * @param event New event
     */
    @PostMapping("/add-event")
    public String projectSave(
            @ModelAttribute("event") Event event,
            Model model
    ) {
        event.setStartDateString(event.getStartDateString());
        event.setEndDateString(event.getEndDateString());
        eventService.addEvent(event);
        return "redirect:/details";
    }

}
