package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utility.EventDic;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/***
 * Controller receives HTTP GET, POST, PUT, DELETE calls for Calendar page
 */
@Controller
public class CalendarController {

    @Autowired
    private SprintService sprintService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EventService eventService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    /***
     * Produces a JSON list that fullcalendar can read to display sprints on the calendar
     * @param sprints list of sprints from the database
     * @return JSON list for sprints to display on the calendar
     */
    public String sprintListToJSON(List<Sprint> sprints) {
        StringBuilder json = new StringBuilder();
        ArrayList<String> colours = new ArrayList<>(Arrays.asList("#5897fc", "#a758fc", "#fc58c3", "#9e1212", "#c65102", "#d5b60a", "#004400"," #11887b"));
        int colIndex = 0;
        for (Sprint sprint : sprints) {
            Date endDate = SprintLifetimeController.getUpdatedDate(sprint.getEndDate(), 1, 0);
            json.append("{id: '").append(sprint.getId()).append("', title: '").append(sprint.getName()).append("', start: '").append(sprint.getStartDate()).append("', end: '").append(endDate.toInstant()).append("', allDay: true, color: '").append(colours.get(colIndex)).append("', type: 'Sprint").append("'},");

            if (colIndex == (colours.size() - 1)) { // List max
                colIndex = 0;
            } else {
                colIndex++;
            }

        }
        return json.toString();
    }

    /***
     * Produces a JSON list that fullcalendar can read to display events on the calendar
     * @param events list of events from the database
     * @return JSON list for events to display on the calendar
     */
    public String eventListToJSON(List<Event> events) {
        StringBuilder json = new StringBuilder();
        for (Event event : events) {
            Date endDate = SprintLifetimeController.getUpdatedDate(event.getEventEndDate(), 1, 0);
            json.append("{id: '").append(event.getId()).append("', title: '").append(event.getEventName()).append("', start: '").append(event.getEventStartDate()).append("', end: '").append(endDate.toInstant()).append("', type: 'Event").append("'},");
        }
        return json.toString();
    }

    /***
     * Produces a JSON list that fullcalendar can read to display events on the calendar
     * @param deadlines list of events from the database
     * @return JSON list for events to display on the calendar
     */
    public String deadlineListToJSON(List<Deadline> deadlines) {
        StringBuilder json = new StringBuilder();
        for (Deadline deadline : deadlines) {
            json.append("{id: '").append(deadline.getId()).append("', title: '").append(deadline.getDeadlineName()).append("', start: '").append(deadline.getDeadlineDate()).append("', type: 'Deadline").append("'},");
        }
        return json.toString();
    }

    /***
     * Produces a JSON list that fullcalendar can read to display events on the calendar
     * @param deadlines list of events from the database
     * @return JSON list for events to display on the calendar
     */
    private String eventsToDisplay(List<Deadline> deadlines, List<Event> events, List<Milestone> milestones) {
        EventDic dic = new EventDic();
        for (Event event : events) {
            dic.add(event);

        }
        for (Deadline deadline : deadlines) {
            dic.add(deadline);
        }
        for (Milestone milestone : milestones) {
            dic.add(milestone);
        }

        return dic.makeJSON();
    }


    /***
     * GET request method, followed by the request URL(../calendar)
     *
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return calendar to view project and sprint dates on
     * @throws Exception
     */
    @GetMapping("/calendar")
    public String calendarPage(
            Model model,
            @AuthenticationPrincipal AuthState principal) throws Exception {
        List<Sprint> sprints;
        List<Event> events;
        List<Deadline> deadlines;
        List<Milestone> milestones;
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        model.addAttribute("userId", id);
        try {
            sprints = sprintService.getAllSprintsOrdered();
            events = eventService.getAllEventsOrdered();
            deadlines = deadlineService.getAllDeadlinesOrdered();
            milestones = milestoneService.getAllMilestonesOrdered();
        } catch (Exception e) {
            return "500InternalServer";
        }
        String calendarEvents = sprintListToJSON(sprints) + eventsToDisplay(deadlines, events, milestones);
        model.addAttribute("events", calendarEvents);

        Project project;
        try {
            project = projectService.getProjectById(0);
        } catch (Exception e) {
            return "404NotFound";
        }
        Date endDate = SprintLifetimeController.getUpdatedDate(project.getEndDate(), 1, 0);
        model.addAttribute("startDate", project.getStartDate());
        model.addAttribute("endDate", endDate);

        model.addAttribute("projectName", project.getName());
        model.addAttribute("projectStartDateString", project.getStartDateString());
        model.addAttribute("projectEndDateString", project.getEndDateString());

        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");


        model.addAttribute("currentUserRole", role);
        return "calendar";
    }


}