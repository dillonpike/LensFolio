package nz.ac.canterbury.seng302.portfolio.service;

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

    /**
     * Returns a blank message if the start date is before or on the end date, otherwise returns an error message.
     * @param startDateString start date to be compared
     * @param endDateString end date to be compared
     * @return blank message if the date is valid, otherwise an error message
     */
    public String validateStartDateNotAfterEndDate(String startDateString, String endDateString) {
        String message = "";
        Date startDate = Project.stringToDate(startDateString);
        Date endDate = Project.stringToDate(endDateString);
        if (startDate.after(endDate)) {
            message = "Start date must be on or before the end date.";
        }
        return message;
    }

    /**
     * Validates the given sprint date range based on the start date and end date of the new sprint, making sure the sprint
     * dates don't overlap with any other sprint.
     * @param sprintStartDateString New sprint start date
     * @param sprintEndDateString New sprint end date
     * @param sprintId Id of the new sprint
     * @return Message giving an error if there is overlaps with other sprints, empty otherwise
     */
    public String validateSprintDateRange(String sprintStartDateString, String sprintEndDateString, int sprintId) {
        String message = "";
        Date sprintStartDate = Project.stringToDate(sprintStartDateString);
        Date sprintEndDate = Project.stringToDate(sprintEndDateString);
        if (sprintStartDate != null && sprintEndDate != null) {
            for (Sprint sprint : sprintService.getAllSprints()) {
                if (sprint.getId() != sprintId && !(
                        sprintStartDate.before(sprint.getStartDate()) && sprintEndDate.before(sprint.getStartDate()) ||
                                sprintStartDate.after(sprint.getEndDate()) && sprintEndDate.after(sprint.getEndDate())
                )) {
                    message = "Dates must not overlap with " + sprint.getName() + "'s dates (" +
                            sprint.getStartDateString() + " - " + sprint.getEndDateString() + ").";
                    break;
                }
            }
        }
        return message;
    }

    /**
     * Validates the given sprint date range based on the start date and end date of the new sprint, making sure the sprint
     * dates are within the project dates
     * @param sprintStartDateString Start date of the sprint
     * @param sprintEndDateString End date of the sprint
     * @return Message giving an error if the dates are not within the project dates, empty otherwise
     */
    public String validateSprintInProjectDateRange(String sprintStartDateString, String sprintEndDateString) {
        String message = "";
        Date sprintStartDate = Project.stringToDate(sprintStartDateString);
        Date sprintEndDate = Project.stringToDate(sprintEndDateString);
        Project project;
        try {
            project = projectService.getProjectById(0);
        } catch (Exception e) {
            System.err.println("No project exists");
            return message;
        }
        if (sprintStartDate.before(project.getStartDate()) || sprintStartDate.after(project.getEndDate()) ||
                sprintEndDate.before(project.getStartDate()) || sprintEndDate.after(project.getEndDate())) {
            message = "Sprint dates must be within the project's date range (" +
                    project.getStartDateString() + " - " + project.getEndDateString() + ").";
        }
        return message;
    }

    /**
     * Returns a blank message if the given date is not over a year ago, otherwise returns an error message.
     * @param dateString date to be checked
     * @return blank message if the date is valid, otherwise an error message
     */
    public String validateDateNotOverAYearAgo(String dateString) {
        String message = "";
        Date date = Project.stringToDate(dateString);
        long diff = getDaysFromNow(date);
        if (diff > 365) {
            message = "Start date must be less than a year ago.";
        }
        return message;
    }

    /**
     * Returns the time difference from now to the given date in days. Returns a positive value if the date is in the
     * past, and a negative value of the date is in the future.
     * @param date date to get the time difference from
     * @return days since the given date from now
     */
    public long getDaysFromNow(Date date){
        Date today = new Date();
        long diffInMs = today.getTime() - date.getTime();
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
