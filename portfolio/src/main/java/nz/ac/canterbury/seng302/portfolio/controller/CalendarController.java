package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarController {

    /***
     * GET request method, followed by the request URL(../calendar)
     *
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return calendar to view project and sprint dates on
     * @throws Exception
     */
    @GetMapping("/calendar")
    public String calendarPage(Model model) throws Exception {

        return "calendar";
}

}
