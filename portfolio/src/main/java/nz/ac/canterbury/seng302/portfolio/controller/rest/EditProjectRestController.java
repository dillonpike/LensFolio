package nz.ac.canterbury.seng302.portfolio.controller.rest;

import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EditProjectRestController {

    /**
     * Service that validates dates.
     */
    @Autowired
    private DateValidationService dateValidationService;

    /**
     * Returns an error message based on the given project dates.
     * @param projectStartDate start date of project
     * @param projectEndDate end date of project
     * @return error message based on the given project dates
     */
    @GetMapping(value="/edit-project/error")
    public String updateProjectRangeErrors(
            @RequestParam(value="projectStartDate") String projectStartDate,
            @RequestParam(value="projectEndDate") String projectEndDate
    ) {
        return dateValidationService.validateDateRangeNotEmpty(projectStartDate, projectEndDate) + " " +
                dateValidationService.validateStartDateNotAfterEndDate(projectStartDate, projectEndDate) + " " +
                dateValidationService.validateDateNotOverAYearAgo(projectStartDate) + " " +
                dateValidationService.validateProjectDatesContainSprints(projectStartDate, projectEndDate);
    }

}
