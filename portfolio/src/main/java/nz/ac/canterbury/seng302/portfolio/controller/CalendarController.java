package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.*;
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
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    public String listToJSON(List<Sprint> sprints) {
        StringBuilder json = new StringBuilder();
        ArrayList<String> colours = new ArrayList<>(Arrays.asList("#5897fc", "#a758fc", "#fc58c3", "#9e1212", "#c65102", "#d5b60a", "#004400"," #11887b"));
        int colIndex = 0;
        for (Sprint sprint : sprints) {
            sprint.setColour(colours.get(colIndex));
            Date endDate = SprintLifetimeController.getUpdatedDate(sprint.getEndDate(), 1, 0);
            json.append("{id: '").append(sprint.getId()).append("', title: '").append(sprint.getName()).append("', start: '").append(sprint.getStartDate()).append("', end: '").append(endDate.toInstant()).append("', allDay: true, color: '").append(colours.get(colIndex)).append("'},");

            if (colIndex == (colours.size() - 1)) { // List max
                colIndex = 0;
            } else {
                colIndex++;
            }

        }
        return json.toString();
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

        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");


        model.addAttribute("currentUserRole", role);
        return "calendar";
    }
}
