package nz.ac.canterbury.seng302.portfolio.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
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
                    message = "Dates must not overlap with " + sprint.getName() + "'s dates (" +
                            sprint.getStartDateString() + " - " + sprint.getEndDateString() + ").";
                    break;
                }
            }
        }
        return message;
    }

    public String validateSprintInProjectDateRange(String startDateString, String endDateString) {
        String message = "";
        Date startDate = Project.stringToDate(startDateString);
        Date endDate = Project.stringToDate(endDateString);
        Project project;
        try {
            project = projectService.getProjectById(0);
        } catch (Exception e) {
            e.printStackTrace();
            return message;
        }
        if (startDate.before(project.getStartDate()) || startDate.after(project.getEndDate()) ||
                endDate.before(project.getStartDate()) || endDate.after(project.getEndDate())) {
            message = "Sprint dates must be within the project's date range (" +
                    project.getStartDateString() + " - " + project.getEndDateString() + ").";
        }
        return message;
    }

    public String validateProjectStartDate(String startDateString, String endDateString) {
        // Checks if the start date is before the end date
        String message = "";
        Date startDate = Project.stringToDate(startDateString);
        Date endDate = Project.stringToDate(endDateString);
        if (startDate.after(endDate)) {
            message =  "Start date must be on or before the end date.";
        }

        // Check the start date is less than a year ago
        long diff = getDiff(startDate);
        if (diff >= 365) {
            message = "Start date must be less than a year ago";
        }

        String startDateIsValid = isDateValid(startDateString);
        String endDateIsValid = isDateValid(endDateString);

        if (! startDateIsValid.equals("")) {
            message = startDateIsValid;
        }

        if (! endDateIsValid.equals("")) {
            message = endDateIsValid;
        }

        return message;
    }

    public long getDiff(Date startDate){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
        String formatDateTime = now.format(formatter);
        Date today = Project.stringToDate(formatDateTime);

        long diffInMs = Math.abs(today.getTime() - startDate.getTime());

        return TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS);
    }

    public String isDateValid(String date) {
        String message = "";
        int day = Integer.parseInt(date.substring(0, 2));
        String month = date.substring(3, 6);
        int year = Integer.parseInt(date.substring(7, 11));


        if ("Feb".equals(month) && (day == 30 || day == 31)) {
            message = "This is an invalid date for February";
        }
        if (year % 4 != 0 && "Feb".equals(month) && day == 29) {
            message = String.format("This is an invalid date for February in the year %d", year);
        }
        if (day == 31 && ("Apr".equals(month) || "Jun".equals(month) || "Sep".equals(month) || "Nov".equals(month))) {
            message = "There is no 31st of the chosen month";
        }

        return message;
    }
}
