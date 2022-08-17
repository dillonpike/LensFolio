package nz.ac.canterbury.seng302.portfolio.service;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Contains help methods to check the validation for date
 */
@Service
public class DateValidationService {

    @Autowired
    private SprintService sprintService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private DeadlineService deadlineService;

    private static final Logger logger = LoggerFactory.getLogger(DateValidationService.class);

    /**
     * Returns an error message if a given date is empty or null, otherwise returns a blank message.
     * @param startDateString start date to be checked
     * @param endDateString end date to be checked
     * @return error message if a given date is empty or null, otherwise returns a blank message
     */
    public String validateDateRangeNotEmpty(String startDateString, String endDateString) {
        String message = "";
        if (startDateString.equals("") || endDateString.equals("")) {
            message = "Dates can't be empty.";
        }
        return message;
    }

    /**
     * Returns an error message if a given time is empty or null, otherwise returns a blank message.
     * @param startTimeString start time to be checked
     * @param endTimeString end time to be checked
     * @return error message if a given time is empty or null, otherwise returns a blank message
     */
    public String validateTimeRangeNotEmpty(String startTimeString, String endTimeString) {
        String message = "";
        if (startTimeString.equals("") || endTimeString.equals("")) {
            message = "Times can't be empty.";
        }
        return message;
    }

    /**
     * Returns a blank message if the start date is before or on the end date, otherwise returns an error message.
     * @param startDateString start date to be compared
     * @param endDateString end date to be compared
     * @return blank message if the date is valid, otherwise an error message
     */
    public String validateStartDateNotAfterEndDate(String startDateString, String endDateString) {
        String message = "";
        if (!startDateString.equals("") && !endDateString.equals("")) {
            Date startDate = Project.stringToDate(startDateString);
            Date endDate = Project.stringToDate(endDateString);
            if (startDate.after(endDate)) {
                message = "Start date must be on or before the end date.";
            }
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
        if (!sprintStartDateString.equals("") && !sprintEndDateString.equals("")) {
            Date sprintStartDate = Project.stringToDate(sprintStartDateString);
            Date sprintEndDate = Project.stringToDate(sprintEndDateString);
            for (Sprint sprint : sprintService.getAllSprints()) {
                // Get date from the string to ignore time
                Date startDate = Project.stringToDate(sprint.getStartDateString());
                Date endDate = Project.stringToDate(sprint.getEndDateString());
                if (sprint.getId() != sprintId && !(
                        sprintStartDate.before(startDate) && sprintEndDate.before(startDate) ||
                                sprintStartDate.after(endDate) && sprintEndDate.after(endDate)
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
     * Validates the given date range based on the start date and end date used for sprints and events, making sure the
     * dates are within the project dates
     * @param sprintStartDateString Start date being checked
     * @param sprintEndDateString End date being checked
     * @return Message giving an error if the dates are not within the project dates, empty otherwise
     */
    public String validateDatesInProjectDateRange(String sprintStartDateString, String sprintEndDateString) {
        String message = "";
        if (!sprintStartDateString.equals("") && !sprintEndDateString.equals("")) {
            Date sprintStartDate = Project.stringToDate(sprintStartDateString);
            Date sprintEndDate = Project.stringToDate(sprintEndDateString);
            Project project;
            try {
                project = projectService.getProjectById(0);
            } catch (Exception e) {
                logger.debug("Project doesn't exist during date validation");
                return message;
            }
            // Get date from the string to ignore time
            Date projectStartDate = Project.stringToDate(project.getStartDateString());
            Date projectEndDate = Project.stringToDate(project.getEndDateString());
            if (sprintStartDate.before(projectStartDate) || sprintStartDate.after(projectEndDate) ||
                    sprintEndDate.before(projectStartDate) || sprintEndDate.after(projectEndDate)) {
                message = "Sprint dates must be within the project's date range (" +
                        project.getStartDateString() + " - " + project.getEndDateString() + ").";
            }
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
        if (!dateString.equals("")) {
            Date date = Project.stringToDate(dateString);
            long diff = getDaysFromNow(date);
            if (diff > 365) {
                message = "Start date must be less than a year ago.";
            }
        }
        return message;
    }

    /**
     * Validates the given project date range based on the dates of the artefacts (sprints, events, milestones, and
     * deadlines), ensuring that the date range contains all the current artefacts.
     * @param projectStartDateString start date of the project
     * @param projectEndDateString end date of the project
     * @return Message giving an error if the project date range doesn't contain all artefacts, empty otherwise
     */
    public String validateProjectDatesContainArtefacts(String projectStartDateString, String projectEndDateString) {
        String message = validateProjectDatesContainSprints(projectStartDateString, projectEndDateString);
        if (message.length() > 0) {
            return message;
        }
        message = validateProjectDatesContainEvents(projectStartDateString, projectEndDateString);
        if (message.length() > 0) {
            return message;
        }
        message = validateProjectDatesContainMilestones(projectStartDateString, projectEndDateString);
        if (message.length() > 0) {
            return message;
        }
        message = validateProjectDatesContainDeadlines(projectStartDateString, projectEndDateString);
        return message;
    }

    /**
     * Validates the given project date range based on the dates of the sprints, ensuring that the date range contains
     * all the current sprints.
     * @param projectStartDateString start date of the project
     * @param projectEndDateString end date of the project
     * @return Message giving an error if the project date range doesn't contain all sprints, empty otherwise
     */
    private String validateProjectDatesContainSprints(String projectStartDateString, String projectEndDateString) {
        String message = "";
        if (!projectStartDateString.equals("") && !projectEndDateString.equals("")) {
            Date projectStartDate = Project.stringToDate(projectStartDateString);
            Date projectEndDate = setToEndOfDay(Project.stringToDate(projectEndDateString));
            List<Sprint> ordered = sprintService.getAllSprintsOrdered();
            if (!ordered.isEmpty()) {
                Sprint first = ordered.get(0);
                Sprint last = ordered.get(ordered.size() - 1);
                if (projectStartDate.after(first.getStartDate()) || projectEndDate.before(last.getEndDate())) {
                    message = "Start date must be on or before the start date of the first sprint (" +
                            first.getStartDateString() + ") and end date must be on or after the end date of " +
                            "the last sprint (" + last.getEndDateString() + ").";
                }
            }
        }
        return message;
    }

    /**
     * Validates the given project date range based on the dates of the events, ensuring that the date range contains
     * all the current events.
     * @param projectStartDateString start date of the project
     * @param projectEndDateString end date of the project
     * @return Message giving an error if the project date range doesn't contain all events, empty otherwise
     */
    private String validateProjectDatesContainEvents(String projectStartDateString, String projectEndDateString) {
        String message = "";
        if (!projectStartDateString.equals("") && !projectEndDateString.equals("")) {
            Date projectStartDate = Project.stringToDate(projectStartDateString);
            Date projectEndDate = setToEndOfDay(Project.stringToDate(projectEndDateString));
            List<Event> orderedStartDate = eventService.getAllEventsOrderedStartDate();
            List<Event> orderedEndDate = eventService.getAllEventsOrderedEndDate();
            if (!orderedStartDate.isEmpty()) {
                Event first = orderedStartDate.get(0);
                Event last = orderedEndDate.get(orderedEndDate.size() - 1);
                if (projectStartDate.after(first.getEventStartDate()) || projectEndDate.before(last.getEventEndDate())) {
                    message = "Start date must be on or before the start date of the first event (" +
                            first.getStartDateString() + ") and end date must be on or after the end date of " +
                            "the last event (" + last.getEndDateString() + ").";
                }
            }
        }
        return message;
    }

    /**
     * Validates the given project date range based on the dates of the milestones, ensuring that the date range contains
     * all the current milestones.
     * @param projectStartDateString start date of the project
     * @param projectEndDateString end date of the project
     * @return Message giving an error if the project date range doesn't contain all milestones, empty otherwise
     */
    private String validateProjectDatesContainMilestones(String projectStartDateString, String projectEndDateString) {
        String message = "";
        if (!projectStartDateString.equals("") && !projectEndDateString.equals("")) {
            Date projectStartDate = Project.stringToDate(projectStartDateString);
            Date projectEndDate = setToEndOfDay(Project.stringToDate(projectEndDateString));
            List<Milestone> ordered = milestoneService.getAllMilestonesOrdered();
            if (!ordered.isEmpty()) {
                Milestone first = ordered.get(0);
                Milestone last = ordered.get(ordered.size() - 1);
                if (projectStartDate.after(first.getMilestoneDate()) || projectEndDate.before(last.getMilestoneDate())) {
                    message = "Start date must be on or before the date of the first milestone (" +
                            first.getMilestoneDateString() + ") and end date must be on or after the date of " +
                            "the last milestone (" + last.getMilestoneDateString() + ").";
                }
            }
        }
        return message;
    }

    /**
     * Validates the given project date range based on the dates of the deadlines, ensuring that the date range contains
     * all the current deadlines.
     * @param projectStartDateString start date of the project
     * @param projectEndDateString end date of the project
     * @return Message giving an error if the project date range doesn't contain all deadlines, empty otherwise
     */
    private String validateProjectDatesContainDeadlines(String projectStartDateString, String projectEndDateString) {
        String message = "";
        if (!projectStartDateString.equals("") && !projectEndDateString.equals("")) {
            Date projectStartDate = Project.stringToDate(projectStartDateString);
            Date projectEndDate = setToEndOfDay(Project.stringToDate(projectEndDateString));
            List<Deadline> ordered = deadlineService.getAllDeadlinesOrdered();
            if (!ordered.isEmpty()) {
                Deadline first = ordered.get(0);
                Deadline last = ordered.get(ordered.size() - 1);
                if (projectStartDate.after(first.getDeadlineDate()) || projectEndDate.before(last.getDeadlineDate())) {
                    message = "Start date must be on or before the date of the first deadline (" +
                            first.getDeadlineDateString() + ") and end date must be on or after the date of " +
                            "the last deadline (" + last.getDeadlineDateString() + ").";
                }
            }
        }
        return message;
    }

    /**
     * Returns the given date but with the time set to 23:59:59.
     * @param date date to be converted
     * @return date with time set to 23:59:59
     */
    private Date setToEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * Returns an error message if a given time is empty or null, otherwise returns a blank message.
     * @param startTimeString start time to be checked
     * @param endTimeString end time to be checked
     * @return error message if a given start time is before an end time, otherwise returns a blank message
     */
    public String validateStartTimeNotAfterEndTime(String startTimeString, String endTimeString, String startDateString,
                                                   String endDateString) {
        String message = "";
        if (!startTimeString.equals("") && !endTimeString.equals("")) {
            LocalTime startTime = Event.stringToTime(startTimeString);
            LocalTime endTime = Event.stringToTime(endTimeString);
            if ((startTime.isAfter(endTime) || startTime == endTime) && startDateString.equals(endDateString)) {
                message = "Start time must be before the end time.";
            }
        }
        return message;
    }

    /**
     * Returns the time difference from now to the given date in days. Returns a positive value if the date is in the
     * past, and a negative value of the date is in the future.
     * @param date date to get the time difference from
     * @return days since the given date from now
     */
    public long getDaysFromNow(Date date) {
        Date today = new Date();
        long diffInMs = today.getTime() - date.getTime();
        return TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS);
    }
}
