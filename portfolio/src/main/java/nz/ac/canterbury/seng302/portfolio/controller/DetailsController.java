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
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*")
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
     * @return projectDetails page
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

        List<List<Event>> eventsForSprints = getAllEventsForAllSprints(sprintList);
        List<List<Deadline>> deadlinesForSprints = getAllDeadlinesForAllSprints(sprintList);
        model.addAttribute("eventsForSprints", eventsForSprints);
        model.addAttribute("deadlinesForSprints", deadlinesForSprints);

        List<List<Milestone>> milestonesForSprints = getAllMilestonesForAllSprints(sprintList);
        model.addAttribute("milestonesForSprints", milestonesForSprints);

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

        List<Milestone> milestoneList = milestoneService.getAllEventsOrderedWithColour(sprintList);
        model.addAttribute("milestones", milestoneList);

        List<Deadline> deadlineList = deadlineService.getAllDeadlinesOrderedWithColour(sprintList);
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

        model.addAttribute("currentUserRole", role);

        model.addAttribute("newSprint", sprintService.getSuggestedSprint());
        model.addAttribute("sprintDateError", "");

        return "projectDetails";
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint.
     * @param message NotificationMessage that holds information about the event being updated
     * @return returns an NotificationResponse that holds information about the event being updated.
     */
    @MessageMapping("/editing-artefact")
    @SendTo("/test/portfolio/artefact/being-edited")
    public NotificationResponse updatingArtefact(NotificationMessage message) {
        int artefactId = message.getArtefactId();
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        String artefactType = message.getArtefactType();
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        return new NotificationResponse(HtmlUtils.htmlEscape(message.getArtefactName()), artefactId, username, firstName, lastName, dateOfNotification, artefactType);
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This is triggered when the user is no longer editing.
     * @param message Information about the editing state.
     * @return Returns the message given.
     */
    @MessageMapping("/stop-editing-artefact")
    @SendTo("/test/portfolio/artefact/stop-being-edited")
    public NotificationResponse stopUpdatingArtefact(NotificationMessage message) {
        int artefactId = message.getArtefactId();
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        String artefactType = message.getArtefactType();
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        return new NotificationResponse(HtmlUtils.htmlEscape(message.getArtefactName()), artefactId, username, firstName, lastName, dateOfNotification, artefactType);
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This method also triggers some sort of re-render of the events.
     * @param message NotificationMessage that holds information about the event being updated
     * @return returns an NotificationResponse that holds information about the event being updated.
     */
    @MessageMapping("/saved-edited-artefact")
    @SendTo("/test/portfolio/artefact/save-edit")
    public NotificationResponse savingUpdatedArtefact(NotificationMessage message) {
        int artefactId = message.getArtefactId();
        String username = message.getUsername();
        String firstName = message.getUserFirstName();
        String lastName = message.getUserLastName();
        long dateOfNotification = Date.from(Instant.now()).toInstant().getEpochSecond();
        String artefactType = message.getArtefactType();
        NotificationResponse response = new NotificationResponse(HtmlUtils.htmlEscape(message.getArtefactName()), artefactId, username, firstName, lastName, dateOfNotification, artefactType);
        // Trigger reload and save the last event's information
        eventsToDisplay.add(response);
        while (eventsToDisplay.size() > 3) {
            eventsToDisplay.remove(0);
        }
        return response;
    }

    /**
     * Gets a list where each element is a list of events that is a part of the sprint from sprintList with the same
     * index.
     * @param sprintList List of sprints to get the events of.
     * @return List of lists of events that are within their given sprint.
     */
    private List<List<Event>> getAllEventsForAllSprints(List<Sprint> sprintList) {
        List<List<Event>> allEventsList = new ArrayList<>();

        for (Sprint sprint : sprintList) {
            allEventsList.add(eventService.getAllEventsOverlappingWithSprint(sprint));
        }

        return allEventsList;
    }

    /**
     * This method used to mainly reload the calendar page when an artefact is being edited or deleted on the project details
     * @param ignore this parameter, even though it is not used, is necessary to exist in order to send the request to websocket
     */
    @MessageMapping("/delete-artefact")
    @SendTo("/test/portfolio/artefact/delete-artefact")
    public NotificationResponse deleteArtefact(NotificationMessage ignore) {
        return new NotificationResponse();
    }

    /**
     * Get a list where each element is a list of deadline that is a part of the sprint from sprintList with the same index
     * @param sprintList List of sprints to get the deadlines of.
     * @return List of lists of deadlines that are within their given sprint
     */
    private List<List<Deadline>> getAllDeadlinesForAllSprints(List<Sprint> sprintList) {
        List<List<Deadline>> allDeadlinesList = new ArrayList<>();

        for (Sprint sprint : sprintList) {
            allDeadlinesList.add(deadlineService.getAllDeadlinesOverLappingWithSprint(sprint));
        }

        return allDeadlinesList;
    }

    /**
     * Gets a list where each element is a list of milestones that is a part of the sprint from sprintList with the same
     * index.
     * @param sprintList List of sprints to get the milestones of.
     * @return List of lists of milestones that are within their given sprint.
     */
    private List<List<Milestone>> getAllMilestonesForAllSprints(List<Sprint> sprintList) {
        List<List<Milestone>> allMilestonesList = new ArrayList<>();

        for (Sprint sprint : sprintList) {
            allMilestonesList.add(milestoneService.getAllMilestonesOverlappingWithSprint(sprint));
        }

        return allMilestonesList;
    }

}
