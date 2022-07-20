package nz.ac.canterbury.seng302.portfolio.controller.rest;

import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Handles REST requests for the calendar page.
 */
@RestController
public class CalendarRestController {

    /**
     * Service that provides methods for sprints.
     */
    @Autowired
    private SprintService sprintService;

    /**
     * Updates the sprint identified by the given id with the given dates.
     * Responds with true if the update is successful, otherwise false.
     * @param id sprint id
     * @param sprintStartDate new start date
     * @param sprintEndDate new end date
     * @return true if the update is successful, otherwise false
     */
    @RequestMapping(value="/update-sprint", method=RequestMethod.POST)
    public boolean updateSprintDates(@RequestParam(value="id") Integer id,
                                     @RequestParam(value="sprintStartDate") String sprintStartDate,
                                     @RequestParam(value="sprintEndDate") String sprintEndDate) {
        return sprintService.updateSprintDates(id, sprintStartDate, sprintEndDate);
    }
}
