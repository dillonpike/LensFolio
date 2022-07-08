package nz.ac.canterbury.seng302.portfolio.controller.rest;

import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles REST requests for adding and removing sprints.
 */
@RestController
public class SprintLifetimeRestController {

    /**
     * Service that validates dates.
     */
    @Autowired
    private DateValidationService dateValidationService;

    /**
     * Returns an error message based on the given sprint dates.
     * @param sprintStartDate start date of sprint
     * @param sprintEndDate end date of sprint
     * @return error message based on the given sprint dates
     */
    @RequestMapping(value="/add-sprint/error", method=RequestMethod.GET)
    public String updateSprintRangeErrors(@RequestParam(value="sprintStartDate") String sprintStartDate,
                                          @RequestParam(value="sprintEndDate") String sprintEndDate) {
        return dateValidationService.validateDateRangeNotEmpty(sprintStartDate, sprintEndDate) + " " +
                        dateValidationService.validateStartDateNotAfterEndDate(sprintStartDate, sprintEndDate) + " " +
                        dateValidationService.validateSprintDateRange(sprintStartDate, sprintEndDate, -1) + " " +
                        dateValidationService.validateDatesInProjectDateRange(sprintStartDate, sprintEndDate);
    }
}
