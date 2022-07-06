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
     * Last event saved, for toast notification.
     */
    private EventResponse eventResponse;

    /**
     * Time that the event was updated.
     */
    private Date eventWasUpdatedTime = Date.from(Instant.now());


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

        // Runs if the reload was triggered by saving an event. Checks set time to now to see if 2 seconds has passed yet.
        long timeDifference = Date.from(Instant.now()).toInstant().getEpochSecond() - eventWasUpdatedTime.toInstant().getEpochSecond();
        if (timeDifference <= 2 && eventResponse != null) {
            model.addAttribute("toastEventInformation", "true");
            model.addAttribute("toastEventName", eventResponse.getEventName());
            model.addAttribute("toastUsername", eventResponse.getUsername());
            model.addAttribute("toastFirstName", eventResponse.getUserFirstName());
            model.addAttribute("toastLastName", eventResponse.getUserLastName());
        } else {
            model.addAttribute("toastEventInformation", "");
        }
        
        List<Sprint> sprintList = sprintService.getAllSprintsOrdered();
        model.addAttribute("sprints", sprintList);

        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        model.addAttribute("userId", id);


        model.addAttribute("blankMilestone", new Milestone());

//        return getFinalThymeleafTemplate(principal);
//        return "teacherProjectDetails";
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

//    /**
//     * Gets final details page to display, depending on whether the user is a teacher or a student.
//     * @param principal Gets information about the user
//     * @return Thymeleaf template
//     */
//    private String getFinalThymeleafTemplate(AuthState principal, Model model) {
//
//    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint.
     * @param message EventMessage that holds information about the event being updated
     * @return returns an EventResponse that holds information about the event being updated.
     */
    @MessageMapping("/editing-event")
    @SendTo("/test/portfolio/events/being-edited")
    public EventResponse updatingEvent(EventMessage message) {
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        return new EventResponse(HtmlUtils.htmlEscape(message.getEventName()), username, firstName, lastName);
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This is triggered when the user is no longer editing.
     * @param message Information about the editing state.
     * @return Returns the message given.
     */
    @MessageMapping("/stop-editing-event")
    @SendTo("/test/portfolio/events/stop-being-edited")
    public String stopUpdatingEvent(String message) {
        return message;
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
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        EventResponse response = new EventResponse(HtmlUtils.htmlEscape(message.getEventName()), username, firstName, lastName);
        // Trigger reload and save the last event's information
        eventWasUpdatedTime = Date.from(Instant.now());
        eventResponse = response;
        return response;
    }

}
