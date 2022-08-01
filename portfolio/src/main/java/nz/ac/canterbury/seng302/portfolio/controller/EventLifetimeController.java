package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.PermissionService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        if (permissionService.isValidToModifyProjectPage(userID)) {
            eventService.addEvent(event);
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
        if (permissionService.isValidToModifyProjectPage(userID)) {
            eventService.removeEvent(id);
        }
        /* Return the name of the Thymeleaf template */
        return "redirect:/details";
    }

}
