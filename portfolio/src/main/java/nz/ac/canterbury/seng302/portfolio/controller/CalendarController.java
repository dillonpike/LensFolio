package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
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
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    public String listToJSON(List<Sprint> sprints) {
        String json = "";
        ArrayList<String> colours = new ArrayList<>(Arrays.asList("#5897fc", "#a758fc", "#fc58c3", "#ff3838", "#ffa538", "#fff64a", "#62ff42"," #42ffb4"));
        int colIndex = 0;
        for (Sprint sprint : sprints) {
            Date endDate = SprintLifetimeController.getUpdatedDate(sprint.getEndDate(), 1, 0);
            json += "{id: '"+sprint.getId()+"', title: '"+sprint.getName()+"', start: '"+sprint.getStartDate()+"', end: '"+endDate.toInstant()+"', allDay: true, color: '"+colours.get(colIndex)+"'},";

            if (colIndex == (colours.size() - 1)) { // List max
                colIndex = 0;
            } else {
                colIndex++;
            }

        }
        return json;
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
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        model.addAttribute("userId", id);
        try {
            sprints = sprintService.getAllSprintsOrdered();
        } catch (Exception e) {
            return "500InternalServer";
        }
        model.addAttribute("sprints", listToJSON(sprints));

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
        return "calendar";
    }
}
