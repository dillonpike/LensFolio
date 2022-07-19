package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
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
import java.time.LocalTime;
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

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private RegisterClientService registerClientService;

    /**
     * Holds list of events information for displaying.
     */
    private ArrayList<NotificationResponse> eventsToDisplay = new ArrayList<>();


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

        List<Sprint> sprintList = sprintService.getAllSprintsOrderedWithColour();
        List<Event> eventList = eventService.getAllEventsOrderedWithColour(sprintList);

        model.addAttribute("sprints", sprintList);
        model.addAttribute("events", eventList);


        // Runs if the reload was triggered by saving an event. Checks the notifications' creation time to see if 2 seconds has passed yet.
        int count = 1;
        ArrayList<NotificationResponse> eventsToDelete = new ArrayList<>();
        for (NotificationResponse event : eventsToDisplay) {
            long timeDifference = Date.from(Instant.now()).toInstant().getEpochSecond() - event.getDateOfCreation();
            if (timeDifference <= 2) {
                model.addAttribute("toastEventInformation" + count, event.getArtefactType());
                model.addAttribute("toastEventName" + count, event.getArtefactName());
                model.addAttribute("toastEventId" + count, event.getArtefactId());
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
        for (NotificationResponse event : eventsToDelete) {
            eventsToDisplay.remove(event);
        }

        List<Milestone> milestoneList = milestoneService.getAllMilestonesOrdered();
        model.addAttribute("milestones", milestoneList);

        List<Deadline> deadlineList = deadlineService.getAllDeadlines();
        model.addAttribute("deadlines", deadlineList);

        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        model.addAttribute("userId", id);
        UserResponse user = registerClientService.getUserData(id);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userFirstName", user.getFirstName());
        model.addAttribute("userLastName", user.getLastName());

        model.addAttribute("newMilestone", new Milestone(0, "", new Date()));

        model.addAttribute("newDeadline", new Deadline(0, "", new Date()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 3);
        model.addAttribute("newEvent", new Event(0, "", new Date(), calendar.getTime(), LocalTime.now(), LocalTime.now()));

        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        model.addAttribute("newSprint", sprintService.getSuggestedSprint());
        model.addAttribute("sprintDateError", "");

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
     * @param message NotificationMessage that holds information about the event being updated
     * @return returns an NotificationResponse that holds information about the event being updated.
     */
    @MessageMapping("/editing-artefact")
    @SendTo("/test/portfolio/events/being-edited")
    public NotificationResponse updatingArtefact(NotificationMessage message) {
        int eventId = message.getArtefactId();
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        String artefactType = message.getArtefactType();
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        return new NotificationResponse(HtmlUtils.htmlEscape(message.getArtefactName()), eventId, username, firstName, lastName, dateOfNotification, artefactType);
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This is triggered when the user is no longer editing.
     * @param message Information about the editing state.
     * @return Returns the message given.
     */
    @MessageMapping("/stop-editing-artefact")
    @SendTo("/test/portfolio/events/stop-being-edited")
    public NotificationResponse stopUpdatingArtefact(NotificationMessage message) {
        int eventId = message.getArtefactId();
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        String artefactType = message.getArtefactType();
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        return new NotificationResponse(HtmlUtils.htmlEscape(message.getArtefactName()), eventId, username, firstName, lastName, dateOfNotification, artefactType);
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This method also triggers some sort of re-render of the events.
     * @param message NotificationMessage that holds information about the event being updated
     * @return returns an NotificationResponse that holds information about the event being updated.
     */
    @MessageMapping("/saved-edited-artefact")
    @SendTo("/test/portfolio/events/save-edit")
    public NotificationResponse savingUpdatedArtefact(NotificationMessage message) {
        int eventId = message.getArtefactId();
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        String artefactType = message.getArtefactType();
        NotificationResponse response = new NotificationResponse(HtmlUtils.htmlEscape(message.getArtefactName()), eventId, username, firstName, lastName, dateOfNotification, artefactType);
        // Trigger reload and save the last event's information
        eventsToDisplay.add(response);
        while (eventsToDisplay.size() > 3) {
            eventsToDisplay.remove(0);
        }
        return response;
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This method also triggers some sort of re-render of the events.
//     * @param message NotificationMessage that holds information about the event being updated
     */
    @MessageMapping("/delete-artefact")
    @SendTo("/test/portfolio/events/delete-artefact")
    public NotificationResponse deleteArtefact(NotificationMessage ignore) {
        //System.out.println("hellow");
//        int eventId = message.getArtefactId();
//        String username = message.getUsername();
//        String firstName = message.getUserFirstName();
//        String lastName = message.getUserLastName();
//        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
//        String artefactType = message.getArtefactType();
//        NotificationResponse response = new NotificationResponse(HtmlUtils.htmlEscape(message.getArtefactName()), eventId, username, firstName, lastName, dateOfNotification, artefactType);
//        // Trigger reload and save the last event's information
//        eventsToDisplay.add(response);
//        while (eventsToDisplay.size() > 3) {
//            eventsToDisplay.remove(0);
//        }
        return new NotificationResponse();
    }

}
