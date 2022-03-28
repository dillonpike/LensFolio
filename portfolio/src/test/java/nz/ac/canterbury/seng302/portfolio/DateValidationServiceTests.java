package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for DateValidationService class.
 */
@Controller
public class DateValidationServiceTests {

    /**
     * DateValidationService object.
     */
    @Autowired
    private DateValidationService dateValidationService = new DateValidationService();

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
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, 5);

        Date future = c.getTime();

        long difference = dateValidationService.getDaysFromNow(future);

        assertTrue(difference < 0);
    }

    /**
     * Checks that the getDaysFromNow method returns a positive value when given a past date.
     */
    @Test
    public void givenPastDate_whenGetDaysFromNow_thenReturnPositiveTime() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -5);

        Date past = c.getTime();

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
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_YEAR, -1);
        c.add(Calendar.YEAR, -1);

        Date oneYearOneDayAgo = c.getTime();

        String output = dateValidationService.validateDateNotOverAYearAgo(Project.dateToString(oneYearOneDayAgo));

        assertTrue(output.length() > 0);
    }

    /**
     * Checks that validateDateNotOverAYearAgo returns a blank message when the date is a year ago (invalid boundary case).
     */
    @Test
    public void givenProjectStartDateAYearAgo_whenValidateDateNotOverAYearAgo_thenOutputWithMessage() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.YEAR, -1);

        Date oneYearAgo = c.getTime();

        String output = dateValidationService.validateDateNotOverAYearAgo(Project.dateToString(oneYearAgo));

        assertEquals(0, output.length());
    }

}
