package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static nz.ac.canterbury.seng302.portfolio.DateTestHelper.addToDate;
import static nz.ac.canterbury.seng302.portfolio.DateTestHelper.addToDateString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DateValidationService class.
 */
@ExtendWith(MockitoExtension.class)
class DateValidationServiceTest {

    /**
     * Mocked ProjectService object.
     */
    @Mock
    private ProjectService projectService = new ProjectService();

    /**
     * Mocked SprintService object.
     */
    @Mock
    private SprintService sprintService = new SprintService();

    /**
     * Mocked EventService object.
     */
    @Mock
    private EventService eventService = new EventService();

    /**
     * Mocked MilestoneService object.
     */
    @Mock
    private MilestoneService milestoneService = new MilestoneService();

    /**
     * Mocked DeadlineService object.
     */
    @Mock
    private DeadlineService deadlineService = new DeadlineService();

    /**
     * DateValidationService object.
     */
    @InjectMocks
    private DateValidationService dateValidationService = new DateValidationService();

    /**
     * Project used for tests.
     */
    private static Project testProject;

    /**
     * List of sprints used for tests.
     */
    private static List<Sprint> testSprints;

    /**
     * List of events used for tests.
     */
    private static List<Event> testEvents;

    /**
     * List of milestones used for tests.
     */
    private static List<Milestone> testMilestones;

    /**
     * List of deadlines used for tests.
     */
    private static List<Deadline> testDeadlines;

    /**
     * Calendar object used for tests.
     */
    private static final Calendar calendar = Calendar.getInstance();

    @BeforeAll
    static void setup() {
        Date today = new Date();
        calendar.setTime(today);
        ArrayList<Date> monthsFromNow = new ArrayList<>(List.of(today));
        for (int i = 0; i < 5; i++) {
            calendar.add(Calendar.MONTH, 1);
            monthsFromNow.add(calendar.getTime());
        }
        testProject = new Project("Test Project", "", monthsFromNow.get(0), monthsFromNow.get(5));

        Sprint testSprint1 = new Sprint(testProject.getId(), "Test Sprint", "1", "", monthsFromNow.get(1), monthsFromNow.get(2));
        Sprint testSprint2 = new Sprint(testProject.getId(), "Test Sprint", "2", "", monthsFromNow.get(3), monthsFromNow.get(4));
        testSprints = List.of(testSprint1, testSprint2);

        Event testEvent1 = new Event(0, testProject.getId(), "Test Event", monthsFromNow.get(1), monthsFromNow.get(2), LocalTime.of(0, 0), LocalTime.of(0, 0));
        Event testEvent2 = new Event(1, testProject.getId(), "Test Event", monthsFromNow.get(3), monthsFromNow.get(4), LocalTime.of(0, 0), LocalTime.of(0, 0));
        testEvents = List.of(testEvent1, testEvent2);

        Milestone testMilestone1 = new Milestone(testProject.getId(), "Test Milestone", monthsFromNow.get(1));
        Milestone testMilestone2 = new Milestone(testProject.getId(), "Test Milestone", monthsFromNow.get(4));
        testMilestones = List.of(testMilestone1, testMilestone2);

        Deadline testDeadline1 = new Deadline(testProject.getId(), "Test Deadline", monthsFromNow.get(1));
        Deadline testDeadline2 = new Deadline(testProject.getId(), "Test Deadline", monthsFromNow.get(4));
        testDeadlines = List.of(testDeadline1, testDeadline2);
    }

    /**
     * Checks that the validateStartDateNotAfterEndDate method returns a blank message when given an identical start and end
     * date (valid boundary case).
     */
    @Test
    void givenValidBoundaryDates_whenValidateStartDateNotAfterEndDate_thenBlankOutput() {
        String startDate = "01/Jan/2022";
        String endDate = "01/Jan/2022";
        String output = dateValidationService.validateStartDateNotAfterEndDate(startDate, endDate);
        assertEquals(0, output.length());
    }

    /**
     * Checks that the validateStartDateNotAfterEndDate method returns an error message when given a start date that occurs
     * after the end date (invalid boundary case).
     */
    @Test
    void givenInvalidBoundaryDates_whenValidateStartDateNotAfterEndDate_thenOutputWithMessage() {
        String startDate = "02/Jan/2022";
        String endDate = "01/Jan/2022";
        String output = dateValidationService.validateStartDateNotAfterEndDate(startDate, endDate);
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that the getDaysFromNow method returns a negative value when given a future date.
     */
    @Test
    void givenFutureDate_whenGetDaysFromNow_thenReturnNegativeTime() {
        Date future = addToDate(new Date(), Calendar.DAY_OF_MONTH, 5);
        long difference = dateValidationService.getDaysFromNow(future);
        assertTrue(difference < 0);
    }

    /**
     * Checks that the getDaysFromNow method returns a positive value when given a past date.
     */
    @Test
    void givenPastDate_whenGetDaysFromNow_thenReturnPositiveTime() {
        Date past = addToDate(new Date(), Calendar.DAY_OF_MONTH, -5);
        long difference = dateValidationService.getDaysFromNow(past);
        assertTrue(difference > 0);
    }

    /**
     * Checks that validateDateNotOverAYearAgo returns an error message when the date is a year and
     * a day ago (invalid boundary case).
     */
    @Test
    void givenProjectStartDateAYearAndADayAgo_whenValidateDateNotOverAYearAgo_thenOutputWithMessage() {
        Date oneYearOneDayAgo = addToDate(addToDate(new Date(), Calendar.DAY_OF_YEAR, -1), Calendar.YEAR, -1);
        String output = dateValidationService.validateDateNotOverAYearAgo(Project.dateToString(oneYearOneDayAgo));
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateDateRangeNotEmpty returns an error message when the given start date is empty.
     */
    @Test
    void givenEmptyStartDate_whenValidateDateRangeNotEmpty_thenOutputWithMessage() {
        String startDate = "";
        String endDate = Project.dateToString(new Date());
        String output = dateValidationService.validateDateRangeNotEmpty(startDate, endDate);
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateDateRangeNotEmpty returns an error message when the given end date is empty.
     */
    @Test
    void givenEmptyEndDate_whenValidateDateRangeNotEmpty_thenOutputWithMessage() {
        String startDate = Project.dateToString(new Date());
        String endDate = "";
        String output = dateValidationService.validateDateRangeNotEmpty(startDate, endDate);
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateDateRangeNotEmpty returns an error message when the given end date is empty.
     */
    @Test
    void givenNotEmptyDates_whenValidateDateRangeNotEmpty_thenBlankOutput() {
        String startDate = Project.dateToString(new Date());
        String endDate = Project.dateToString(new Date());
        String output = dateValidationService.validateDateRangeNotEmpty(startDate, endDate);
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateDatesInProjectDateRange gives a blank output when the given dates are within the project's
     * dates (boundary case).
     */
    @Test
    void givenValidBoundaryDates_whenValidateSprintInProjectDateRange_thenBlankOutput() throws Exception {
        when(projectService.getProjectById(anyInt())).thenReturn(testProject);
        String output = dateValidationService.validateDatesInProjectDateRange(testProject.getStartDateString(),
                testProject.getEndDateString());
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateDatesInProjectDateRange gives an error message when the given start date is outside the
     * project's dates (boundary case).
     */
    @Test
    void givenInvalidStartDate_whenValidateSprintInProjectDateRange_thenOutputWithMessage() throws Exception {
        when(projectService.getProjectById(anyInt())).thenReturn(testProject);
        String startDateString = addToDateString(testProject.getStartDateString(), Calendar.DAY_OF_YEAR, -1);
        String output = dateValidationService.validateDatesInProjectDateRange(startDateString,
                testProject.getEndDateString());
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateDatesInProjectDateRange gives an error message when the given end date is outside the
     * project's dates (boundary case).
     */
    @Test
    void givenInvalidEndDate_whenValidateSprintInProjectDateRange_thenOutputWithMessage() throws Exception {
        when(projectService.getProjectById(anyInt())).thenReturn(testProject);
        String endDateString = addToDateString(testProject.getEndDateString(), Calendar.DAY_OF_YEAR, 1);
        String output = dateValidationService.validateDatesInProjectDateRange(testProject.getStartDateString(),
                endDateString);
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateSprintDateRange gives an error message when the given end date is the same as the start
     * date of an already existing sprint (boundary case).
     */
    @Test
    void givenOverlappingWithStartDate_whenValidateSprintDateRange_thenOutputWithMessage() {
        when(sprintService.getAllSprints()).thenReturn(testSprints);
        Sprint testSprint = testSprints.get(0);
        String output = dateValidationService.validateSprintDateRange(
                addToDateString(testSprint.getStartDateString(), Calendar.DAY_OF_YEAR, -1),
                testSprint.getStartDateString(), -1);
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateSprintDateRange gives an error message when the given start date is the same as the end
     * date of an already existing sprint (boundary case).
     */
    @Test
    void givenOverlappingWithEndDate_whenValidateSprintDateRange_thenOutputWithMessage() {
        when(sprintService.getAllSprints()).thenReturn(testSprints);
        Sprint testSprint = testSprints.get(1);
        String output = dateValidationService.validateSprintDateRange(testSprint.getEndDateString(),
                addToDateString(testSprint.getEndDateString(), Calendar.DAY_OF_YEAR, 1), -1);
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateSprintDateRange gives a blank output when the given dates do not overlap with any
     * existing sprints (boundary case).
     */
    @Test
    void givenValidDates_whenValidateSprintDateRange_thenBlankOutput() {
        when(sprintService.getAllSprints()).thenReturn(testSprints);
        Sprint testSprint = testSprints.get(0);
        String output = dateValidationService.validateSprintDateRange(
                addToDateString(testSprint.getEndDateString(), Calendar.DAY_OF_YEAR, 1),
                addToDateString(testSprint.getEndDateString(), Calendar.DAY_OF_YEAR, 2), -1);
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateSprintDateRange gives a blank output when the given dates overlap with its own stored sprint.
     * This is for editing, so the dates of the sprint currently being edited are ignored.
     */
    @Test
    void givenOverlappingDatesWithCurrentSprint_whenValidateSprintDateRange_thenBlankOutput() {
        when(sprintService.getAllSprints()).thenReturn(testSprints);
        Sprint testSprint = testSprints.get(1);
        String output = dateValidationService.validateSprintDateRange(testSprint.getStartDateString(),
                testSprint.getEndDateString(), testSprint.getId());
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives a blank output when the given dates start on the same day
     * as the first sprint and end on the same day as the last sprint (boundary valid case).
     */
    @Test
    void givenValidBoundaryDatesSprint_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(sprintService.getAllSprintsOrdered()).thenReturn(testSprints);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                testProject.getStartDateString(), testProject.getEndDateString());
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the start date is the day after the
     * start date of the first sprint (boundary invalid case).
     */
    @Test
    void givenInvalidStartDateSprint_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(sprintService.getAllSprintsOrdered()).thenReturn(testSprints);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                addToDateString(testSprints.get(0).getStartDateString(), Calendar.DAY_OF_YEAR, 1),
                testProject.getEndDateString());
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the end date is the day before the
     * end date of the last sprint (boundary invalid case).
     */
    @Test
    void givenInvalidEndDateSprint_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(sprintService.getAllSprintsOrdered()).thenReturn(testSprints);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                testProject.getStartDateString(),
                addToDateString(testSprints.get(1).getEndDateString(), Calendar.DAY_OF_YEAR, -1));
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the dates given are between two
     * sprints (invalid case).
     */
    @Test
    void givenInvalidDatesSprint_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(sprintService.getAllSprintsOrdered()).thenReturn(testSprints);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                addToDateString(testSprints.get(0).getEndDateString(), Calendar.DAY_OF_YEAR, 1),
                addToDateString(testSprints.get(1).getStartDateString(), Calendar.DAY_OF_YEAR, -1));
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives a blank output when the given dates start on the same day
     * as the first event and end on the same day as the last event (boundary valid case).
     */
    @Test
    void givenValidBoundaryDatesEvent_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(eventService.getAllEventsOrderedStartDate()).thenReturn(testEvents);
        when(eventService.getAllEventsOrderedEndDate()).thenReturn(testEvents);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                testProject.getStartDateString(), testProject.getEndDateString());
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the start date is the day after the
     * start date of the first event (boundary invalid case).
     */
    @Test
    void givenInvalidStartDateEvent_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(eventService.getAllEventsOrderedStartDate()).thenReturn(testEvents);
        when(eventService.getAllEventsOrderedEndDate()).thenReturn(testEvents);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                addToDateString(testEvents.get(0).getStartDateString(), Calendar.DAY_OF_YEAR, 1),
                testProject.getEndDateString());
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the end date is the day before the
     * end date of the last event (boundary invalid case).
     */
    @Test
    void givenInvalidEndDateEvent_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(eventService.getAllEventsOrderedStartDate()).thenReturn(testEvents);
        when(eventService.getAllEventsOrderedEndDate()).thenReturn(testEvents);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                testProject.getStartDateString(),
                addToDateString(testEvents.get(1).getEndDateString(), Calendar.DAY_OF_YEAR, -1));
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the dates given are between two
     * events (invalid case).
     */
    @Test
    void givenInvalidDatesEvent_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(eventService.getAllEventsOrderedStartDate()).thenReturn(testEvents);
        when(eventService.getAllEventsOrderedEndDate()).thenReturn(testEvents);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                addToDateString(testEvents.get(0).getEndDateString(), Calendar.DAY_OF_YEAR, 1),
                addToDateString(testEvents.get(1).getStartDateString(), Calendar.DAY_OF_YEAR, -1));
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives a blank output when the given dates start on the same day
     * as the first event and end on the same day as the last event (boundary valid case).
     */
    @Test
    void givenValidBoundaryDatesMilestone_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(milestoneService.getAllMilestonesOrdered()).thenReturn(testMilestones);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                testProject.getStartDateString(), testProject.getEndDateString());
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the start date is the day after the
     * start date of the first event (boundary invalid case).
     */
    @Test
    void givenInvalidStartDateMilestone_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(milestoneService.getAllMilestonesOrdered()).thenReturn(testMilestones);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                addToDateString(testMilestones.get(0).getMilestoneDateString(), Calendar.DAY_OF_YEAR, 1),
                testProject.getEndDateString());
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the end date is the day before the
     * end date of the last event (boundary invalid case).
     */
    @Test
    void givenInvalidEndDateMilestone_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(milestoneService.getAllMilestonesOrdered()).thenReturn(testMilestones);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                testProject.getStartDateString(),
                addToDateString(testMilestones.get(1).getMilestoneDateString(), Calendar.DAY_OF_YEAR, -1));
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the dates given are between two
     * events (invalid case).
     */
    @Test
    void givenInvalidDatesMilestone_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(milestoneService.getAllMilestonesOrdered()).thenReturn(testMilestones);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                addToDateString(testMilestones.get(0).getMilestoneDateString(), Calendar.DAY_OF_YEAR, 1),
                addToDateString(testMilestones.get(1).getMilestoneDateString(), Calendar.DAY_OF_YEAR, -1));
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives a blank output when the given dates start on the same day
     * as the first event and end on the same day as the last event (boundary valid case).
     */
    @Test
    void givenValidBoundaryDatesDeadline_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(deadlineService.getAllDeadlinesOrdered()).thenReturn(testDeadlines);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                testProject.getStartDateString(), testProject.getEndDateString());
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the start date is the day after the
     * start date of the first event (boundary invalid case).
     */
    @Test
    void givenInvalidStartDateDeadline_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(deadlineService.getAllDeadlinesOrdered()).thenReturn(testDeadlines);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                addToDateString(testDeadlines.get(0).getDeadlineDateString(), Calendar.DAY_OF_YEAR, 1),
                testProject.getEndDateString());
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the end date is the day before the
     * end date of the last event (boundary invalid case).
     */
    @Test
    void givenInvalidEndDateDeadline_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(deadlineService.getAllDeadlinesOrdered()).thenReturn(testDeadlines);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                testProject.getStartDateString(),
                addToDateString(testDeadlines.get(1).getDeadlineDateString(), Calendar.DAY_OF_YEAR, -1));
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateProjectDatesContainArtefacts gives an error message when the dates given are between two
     * events (invalid case).
     */
    @Test
    void givenInvalidDatesDeadline_whenValidateProjectDatesContainArtefacts_thenBlankOutput() {
        when(deadlineService.getAllDeadlinesOrdered()).thenReturn(testDeadlines);
        String output = dateValidationService.validateProjectDatesContainArtefacts(
                addToDateString(testDeadlines.get(0).getDeadlineDateString(), Calendar.DAY_OF_YEAR, 1),
                addToDateString(testDeadlines.get(1).getDeadlineDateString(), Calendar.DAY_OF_YEAR, -1));
        assertTrue(output.length() > 0);
    }
}
