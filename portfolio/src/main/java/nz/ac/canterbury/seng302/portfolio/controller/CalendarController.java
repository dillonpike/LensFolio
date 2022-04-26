package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

@Controller
public class CalendarController {

    @Autowired
    private ProjectService projectService;

    /***
     * GET request method, followed by the request URL(../calendar)
     *
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return calendar to view project and sprint dates on
     * @throws Exception
     */
    @GetMapping("/calendar")
    public String calendarPage(Model model) throws Exception {
        Project project;
        try {
            project = projectService.getProjectById(0);
        } catch (Exception e) {
            return "404NotFound";
        }
        Date endDate = SprintLifetimeController.getUpdatedDate(project.getEndDate(), 1, 0);
        model.addAttribute("startDate", project.getStartDate());
        model.addAttribute("endDate", endDate);
        return "calendar";
}

}
