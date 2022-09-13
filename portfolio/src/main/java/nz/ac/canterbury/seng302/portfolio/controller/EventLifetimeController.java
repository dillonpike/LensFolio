package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.NotAcceptableException;


/**
 * Controller for the add event page
 */
@Controller
public class EventLifetimeController {

    private static final Logger logger = LoggerFactory.getLogger(EventLifetimeController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    /**
     * Tries to save the new event to the database
     * @param event New event
     */
    @PostMapping("/add-event")
    public String projectSave(
            @ModelAttribute("event") Event event,
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);
        try {
            eventService.validateEvent(event, model);
            if (permissionService.isValidToModify(userID)) {
                eventService.addEvent(event);
            }
        } catch (NotAcceptableException e) {
            logger.error(String.format("Error adding event: %s", e.getMessage()));
        }
        return "redirect:/details";
    }


    /***
     * Request handler for deleting event, user will redirect to project detail page after
     * @param id Event Id
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return project detail page
     */
    @GetMapping("/delete-event/{id}")
    public String sprintRemove(@PathVariable("id") Integer id,
                               @AuthenticationPrincipal AuthState principal,
                               Model model) {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);
        if (permissionService.isValidToModify(userID)) {
            eventService.removeEvent(id);
        }
        /* Return the name of the Thymeleaf template */
        return "redirect:/details";
    }

}
