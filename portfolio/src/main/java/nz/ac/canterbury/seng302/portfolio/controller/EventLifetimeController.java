package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


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
     * Tries to save the new event to the database
     * @param event New event
     */
    @PostMapping("/add-event")
    public String projectSave(
            @ModelAttribute("event") Event event,
            Model model
    ) {
        eventService.addEvent(event);
        return "redirect:/details";
    }


    /***
     * Request handler for deleting event, user will redirect to project detail page after
     * @param id Event Id
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return project detail page
     */
    @GetMapping("/delete-event/{id}")
    public String sprintRemove(@PathVariable("id") Integer id, Model model) {
        eventService.removeEvent(id);

        /* Return the name of the Thymeleaf template */
        return "redirect:/details";
    }

}
