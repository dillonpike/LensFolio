package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utility.EventDic;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.apache.http.HttpException;
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

    @Autowired
    private RegisterClientService registerClientService;

    private static final String MESSAGE_ID = "{id: '";
    private static final String MESSAGE_TITLE = "', title: '";
    private static final String MESSAGE_START = "', start: '";

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
            sprint.setColour(colours.get(colIndex));
            Date endDate = SprintLifetimeController.getUpdatedDate(sprint.getEndDate(), 1, 0);
            json.append(MESSAGE_ID).append(sprint.getId()).append(MESSAGE_TITLE).append(sprint.getName()).append(MESSAGE_START).append(sprint.getStartDate()).append("', end: '").append(endDate.toInstant()).append("', allDay: true, color: '").append(colours.get(colIndex)).append("', type: 'Sprint").append("'},");

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
            json.append(MESSAGE_ID).append(event.getId()).append(MESSAGE_TITLE).append(event.getEventName()).append(MESSAGE_START).append(event.getEventStartDate()).append("', end: '").append(endDate.toInstant()).append("', type: 'Event").append("'},");
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
            json.append(MESSAGE_ID).append(deadline.getId()).append(MESSAGE_TITLE).append(deadline.getDeadlineName()).append(MESSAGE_START).append(deadline.getDeadlineDate()).append("', type: 'Deadline").append("'},");
        }
        return json.toString();
    }

    /***
     * Produces a JSON list that fullcalendar can read to display events on the calendar
     * @param deadlines list of events from the database
     * @return JSON list for events to display on the calendar
     */
    private String eventsToDisplay(List<Deadline> deadlines, List<Event> events, List<Milestone> milestones) {
        EventDic dic1 = new EventDic();
        for (Event event : events) {
            dic1.add(event);
        }
        for (Deadline deadline : deadlines) {
            dic1.add(deadline);
        }
        for (Milestone milestone : milestones) {
            dic1.add(milestone);
        }

        return dic1.makeJSON();
    }

    /**
     * Adjusted date to start at the first day of the month.
     *
     * @param realDate The date to get full month of.
     * @return Adjusted date object
     */
    public Date getStartMonths(Date realDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(realDate);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Adjusted date to start at the last day of the month.
     *
     * @param realDate The date to get full month of.
     * @return Adjusted date object
     */
    public Date getEndMonths(Date realDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(realDate);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * This method is used to set all the data needed for calendar view.
     * @throws HttpException The http exception thrown if the something fails.
     */
    private void setCalendarData(Model model, AuthState principal) throws HttpException {
        List<Sprint> sprints;
        List<Event> events;
        List<Deadline> deadlines;
        List<Milestone> milestones;
        try {
            sprints = sprintService.getAllSprintsOrdered();
            events = eventService.getAllEventsOrderedStartDate();
            deadlines = deadlineService.getAllDeadlinesOrdered();
            milestones = milestoneService.getAllMilestonesOrdered();
        } catch (Exception e) {
            throw new HttpException("500InternalServer");
        }
        String calendarEvents = sprintListToJSON(sprints) + eventsToDisplay(deadlines, events, milestones);
        model.addAttribute("events", calendarEvents);
        Project project;
        try {
            project = projectService.getProjectById(0);
        } catch (Exception e) {
            throw new HttpException("404NotFound");
        }
        Date endDate = SprintLifetimeController.getUpdatedDate(project.getEndDate(), 1, 0);
        model.addAttribute("startDate", project.getStartDate());
        model.addAttribute("endDate", endDate);
        model.addAttribute("trueEndDate", project.getEndDate());
        model.addAttribute("projectName", project.getName());
        model.addAttribute("projectStartDateString", project.getStartDateString());
        model.addAttribute("projectEndDateString", project.getEndDateString());
        model.addAttribute("fullStartDate", getStartMonths(project.getStartDate()));
        model.addAttribute("fullEndDate", getEndMonths(endDate));


        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");


        model.addAttribute("currentUserRole", role);
    }


    /***
     * GET request method, followed by the request URL(../calendar)
     *
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return calendar to view project and sprint dates on
     */
    @GetMapping("/calendar")
    public String calendarPage(
            Model model,
            @AuthenticationPrincipal AuthState principal) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        model.addAttribute("userId", id);
        UserResponse user = registerClientService.getUserData(id);
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userFirstName", user.getFirstName());
        model.addAttribute("userLastName", user.getLastName());
        try {
            setCalendarData(model, principal);
        } catch (HttpException e) {
            return e.getMessage();
        }
        return "calendar";
    }

    /**
     * This method is used to update calendar data. It is called when user made any update on project page that might affect the calendar.
     * @param model The model to be updated.
     * @param principal Authentication state of the user.
     * @return the fragment of the calendar to be updated inside calendar page.
     */
    @GetMapping("/update-calendar")
    public String updateCalendarPage(
            Model model,
            @AuthenticationPrincipal AuthState principal) {
        try {
            setCalendarData(model, principal);
        } catch (HttpException e) {
            return e.getMessage();
        }

        return "calendar::calendar_body";
    }


}