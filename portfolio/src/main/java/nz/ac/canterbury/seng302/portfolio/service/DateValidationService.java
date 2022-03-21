package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DateValidationService {
    @Autowired
    private SprintService sprintService;
    @Autowired
    private ProjectService projectService;

    public String validateSprintStartDate(String startDateString, String endDateString) {
        String message = "";
        Date startDate = Project.stringToDate(startDateString);
        Date endDate = Project.stringToDate(endDateString);
        if (startDate.after(endDate)) {
            message =  "Start date must be on or before the end date.";
        }
        return message;
    }

    public String validateSprintDateRange(String startDateString, String endDateString, int sprintId) {
        String message = "";
        Date startDate = Project.stringToDate(startDateString);
        Date endDate = Project.stringToDate(endDateString);
        if (startDate != null && endDate != null) {
            for (Sprint sprint : sprintService.getAllSprints()) {
                if (sprint.getId() != sprintId && !(
                        startDate.before(sprint.getStartDate()) && endDate.before(sprint.getStartDate()) ||
                                startDate.after(sprint.getEndDate()) && endDate.after(sprint.getEndDate())
                )) {
                    message = "Dates must not overlap with " + sprint.getName() + "'s dates\n(" +
                            sprint.getStartDateString() + " - " + sprint.getEndDateString() + ").";
                    break;
                }
            }
        }
        return message;
    }

    public String validateProjectStartDate(String startDateString, String endDateString) {
        String message = "";
        Date startDate = Project.stringToDate(startDateString);
        Date endDate = Project.stringToDate(endDateString);
        if (startDate.after(endDate)) {
            message =  "Start date must be on or before the end date.";
        }
        return message;
    }
}
