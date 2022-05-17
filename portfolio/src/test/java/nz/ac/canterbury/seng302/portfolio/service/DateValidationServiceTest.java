package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.UserSortingRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DateValidationService class.
 */
@ExtendWith(MockitoExtension.class)
public class DateValidationServiceTest {

    /**
     * Mocked ProjectService object.
     */
    @Mock
    private ProjectService projectService = new ProjectService();

    /**
     * DateValidationService object.
     */
    @InjectMocks
    private DateValidationService dateValidationService = new DateValidationService();

    private static Project testProject;

    private static final Calendar calendar = Calendar.getInstance();

    @BeforeAll
    public static void setup() {
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, 3);
        Date threeMonthsFromNow = calendar.getTime();
        testProject = new Project("Test Project", "", today, threeMonthsFromNow);
    }

    /**
     * Checks that the validateStartDateNotAfterEndDate method returns a blank message when given an identical start and end
     * date (valid boundary case).
     */
    @Test
    public void givenValidBoundaryDates_whenValidateStartDateNotAfterEndDate_thenBlankOutput() {
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
    public void givenInvalidBoundaryDates_whenValidateStartDateNotAfterEndDate_thenOutputWithMessage() {
        String startDate = "02/Jan/2022";
        String endDate = "01/Jan/2022";
        String output = dateValidationService.validateStartDateNotAfterEndDate(startDate, endDate);

        assertTrue(output.length() > 0);
    }

    /**
     * Checks that the getDaysFromNow method returns a negative value when given a future date.
     */
    @Test
    public void givenFutureDate_whenGetDaysFromNow_thenReturnNegativeTime() {
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, 5);

        Date future = calendar.getTime();

        long difference = dateValidationService.getDaysFromNow(future);

        assertTrue(difference < 0);
    }

    /**
     * Checks that the getDaysFromNow method returns a positive value when given a past date.
     */
    @Test
    public void givenPastDate_whenGetDaysFromNow_thenReturnPositiveTime() {
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -5);

        Date past = calendar.getTime();

        long difference = dateValidationService.getDaysFromNow(past);

        assertTrue(difference > 0);
    }

    /**
     * Checks that validateDateNotOverAYearAgo returns an error message when the date is a year and
     * a day ago (invalid boundary case).
     */
    @Test
    public void givenProjectStartDateAYearAndADayAgo_whenValidateDateNotOverAYearAgo_thenOutputWithMessage() {
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.add(Calendar.YEAR, -1);

        Date oneYearOneDayAgo = calendar.getTime();

        String output = dateValidationService.validateDateNotOverAYearAgo(Project.dateToString(oneYearOneDayAgo));

        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateDateRangeNotEmpty returns an error message when the given start date is empty.
     */
    @Test
    public void givenEmptyStartDate_whenValidateDateRangeNotEmpty_thenOutputWithMessage() {
        String startDate = "";
        String endDate = Project.dateToString(new Date());
        String output = dateValidationService.validateDateRangeNotEmpty(startDate, endDate);
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateDateRangeNotEmpty returns an error message when the given end date is empty.
     */
    @Test
    public void givenEmptyEndDate_whenValidateDateRangeNotEmpty_thenOutputWithMessage() {
        String startDate = Project.dateToString(new Date());
        String endDate = "";
        String output = dateValidationService.validateDateRangeNotEmpty(startDate, endDate);
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateDateRangeNotEmpty returns an error message when the given end date is empty.
     */
    @Test
    public void givenNotEmptyDates_whenValidateDateRangeNotEmpty_thenBlankOutput() {
        String startDate = Project.dateToString(new Date());
        String endDate = Project.dateToString(new Date());
        String output = dateValidationService.validateDateRangeNotEmpty(startDate, endDate);
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateSprintInProjectDateRange gives a blank output when the given dates are within the project's
     * dates (boundary case).
     */
    @Test
    public void givenValidBoundaryDates_whenValidateSprintInProjectDateRange_thenBlankOutput() throws Exception {
        when(projectService.getProjectById(anyInt())).thenReturn(testProject);
        String output = dateValidationService.validateSprintInProjectDateRange(testProject.getStartDateString(),
                testProject.getEndDateString());
        assertEquals(0, output.length());
    }

    /**
     * Checks that validateSprintInProjectDateRange gives an error message when the given start date is outside the
     * project's dates (boundary case).
     */
    @Test
    public void givenInvalidStartDate_whenValidateSprintInProjectDateRange_thenOutputWithMessage() throws Exception {
        when(projectService.getProjectById(anyInt())).thenReturn(testProject);
        Date startDate = testProject.getStartDate();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String startDateString = Project.dateToString(calendar.getTime());
        String output = dateValidationService.validateSprintInProjectDateRange(startDateString,
                testProject.getEndDateString());
        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateSprintInProjectDateRange gives an error message when the given end date is outside the
     * project's dates (boundary case).
     */
    @Test
    public void givenInvalidEndDate_whenValidateSprintInProjectDateRange_thenOutputWithMessage() throws Exception {
        when(projectService.getProjectById(anyInt())).thenReturn(testProject);
        Date endDate = testProject.getEndDate();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String endDateString = Project.dateToString(calendar.getTime());
        String output = dateValidationService.validateSprintInProjectDateRange(testProject.getStartDateString(),
                endDateString);
        assertTrue(output.length() > 0);
    }

}
