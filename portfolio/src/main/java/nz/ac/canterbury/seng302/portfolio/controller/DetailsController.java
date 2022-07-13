package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.web.util.HtmlUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Controller for the display project details page
 */
@Controller
public class DetailsController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    @Autowired
    private EventService eventService;

    /**
     * Holds list of events information for displaying.
     */
    private ArrayList<EventResponse> eventsToDisplay = new ArrayList<>();


    /***
     * GET request method, followed by the request URL(../details)
     *
     * @param principal
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return TeacherProjectDetails or userProjectDetails which is dependent on user's role
     * @throws Exception
     */
    @GetMapping("/details")
    public String details(@AuthenticationPrincipal AuthState principal, Model model) throws Exception {
        /* Add project details to the model */
        // Gets the project with id 0 to plonk on the page
        Project project;
        try {
            project = projectService.getProjectById(0);
        } catch (Exception e) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Instant time = Instant.now();
            Timestamp dateAdded = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).build();
            LocalDate endDate = time.atZone(ZoneId.systemDefault()).toLocalDate();
            Date date8Months = java.sql.Date.valueOf(endDate.plusMonths(8));  // 8 months after the current date
            project = new Project(
                    "Project " + currentYear,
                    "Default Project",
                    new Date(dateAdded.getSeconds() * 1000),
                    date8Months
            );
            project.setId(0);
            try {
                projectService.saveProject(project);
            } catch (Exception err) {
                return "redirect:account";
            }

        }

        model.addAttribute("project", project);
        model.addAttribute("project", project);

        List<Event> eventList = eventService.getAllEventsOrdered();
        model.addAttribute("events", eventList);

        // Runs if the reload was triggered by saving an event. Checks the notifications' creation time to see if 2 seconds has passed yet.
        int count = 1;
        ArrayList<EventResponse> eventsToDelete = new ArrayList<>();
        for (EventResponse event : eventsToDisplay) {
            long timeDifference = Date.from(Instant.now()).toInstant().getEpochSecond() - event.getDateOfCreation();
            if (timeDifference <= 2) {
                model.addAttribute("toastEventInformation" + count, "true");
                model.addAttribute("toastEventName" + count, event.getEventName());
                model.addAttribute("toastEventId" + count, event.getEventId());
                model.addAttribute("toastUsername" + count, event.getUsername());
                model.addAttribute("toastFirstName" + count, event.getUserFirstName());
                model.addAttribute("toastLastName" + count, event.getUserLastName());
            } else {
                eventsToDelete.add(event);
                model.addAttribute("toastEventInformation" + count, "");
                model.addAttribute("toastEventName" + count, "");
                model.addAttribute("toastEventId" + count, "");
                model.addAttribute("toastUsername" + count, "");
                model.addAttribute("toastFirstName" + count, "");
                model.addAttribute("toastLastName" + count, "");
            }
            count++;
        }
        for (EventResponse event : eventsToDelete) {
            eventsToDisplay.remove(event);
        }
        
        List<Sprint> sprintList = sprintService.getAllSprintsOrdered();
        model.addAttribute("sprints", sprintList);

        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        model.addAttribute("userId", id);

        return getFinalThymeleafTemplate(principal);
    }

    /**
     * Returns a string that represents the correct Thymeleaf template for the user, depending on whether the user
     * has a role above student.
     * @param principal
     * @return
     */
    private String getFinalThymeleafTemplate(AuthState principal) {
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        /* Return the name of the Thymeleaf template */
        // detects the role of the current user and returns appropriate page
        if (role.equals("teacher") || role.equals("admin")) {
            return "teacherProjectDetails";
        } else {
            return "userProjectDetails";
        }
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint.
     * @param message EventMessage that holds information about the event being updated
     * @return returns an EventResponse that holds information about the event being updated.
     */
    @MessageMapping("/editing-event")
    @SendTo("/test/portfolio/events/being-edited")
    public EventResponse updatingEvent(EventMessage message) {
        int eventId = message.getEventId();
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        return new EventResponse(HtmlUtils.htmlEscape(message.getEventName()), eventId, username, firstName, lastName, dateOfNotification);
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This is triggered when the user is no longer editing.
     *
     * @param message Information about the editing state.
     * @return Returns the message given.
     */
    @MessageMapping("/stop-editing-event")
    @SendTo("/test/portfolio/events/stop-being-edited")
    public EventResponse stopUpdatingEvent(EventMessage message) {
        int eventId = message.getEventId();
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        return new EventResponse(HtmlUtils.htmlEscape(message.getEventName()), eventId, username, firstName, lastName, dateOfNotification);
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This method also triggers some sort of re-render of the events.
     * @param message EventMessage that holds information about the event being updated
     * @return returns an EventResponse that holds information about the event being updated.
     */
    @MessageMapping("/saved-edited-event")
    @SendTo("/test/portfolio/events/save-edit")
    public EventResponse savingUpdatedEvent(EventMessage message) {
        int eventId = message.getEventId();
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        EventResponse response = new EventResponse(HtmlUtils.htmlEscape(message.getEventName()), eventId, username, firstName, lastName, dateOfNotification);
        // Trigger reload and save the last event's information
        eventsToDisplay.add(response);
        while (eventsToDisplay.size() > 3) {
            eventsToDisplay.remove(0);
        }
        return response;
    }
}
