package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAcceptableException;

/**
 * Controller receive HTTP GET, POST, PUT, DELETE calls for edit event
 */
@Controller
public class EditEventController {

    private static final Logger logger = LoggerFactory.getLogger(EditEventController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private DateValidationService dateValidationService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ElementService elementService;

    /**
     * Tries to save new data to event with given eventId to the database.
     * @param id Id of event edited
     * @param event Event data to be updated
     * @throws ObjectNotFoundException if event cannot be found from the given ID or if it cannot be saved.
     */
    @PostMapping("/edit-event/{id}")
    public String eventEditSave(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal AuthState principal,
            @ModelAttribute("event") Event event,
            RedirectAttributes rm,
            Model model,
            HttpServletResponse httpServletResponse
    ) throws ObjectNotFoundException {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);

        try {
            eventService.validateEvent(event, model);
            if (permissionService.isValidToModify(userID)) {
                eventService.updateEvent(event);
            } else {
                rm.addFlashAttribute("isAccessDenied", true);
            }
        } catch (NotAcceptableException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(String.format("Error occurred while trying to save event with id %d: %s", id, e.getMessage()));
            return "fragments/eventModal::eventModalBody";
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        return "fragments/eventModal::eventModalBody";
    }

}
