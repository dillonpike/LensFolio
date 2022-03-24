package nz.ac.canterbury.seng302.portfolio;

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
     * Checks that the validateSprintStartDate method returns a blank message when given an identical start and end
     * date.
     */
    @Test
    public void givenValidBoundaryDates_whenValidateSprintStartDate_thenBlankOutput() {
        String startDate = "01/Jan/2022";
        String endDate = "01/Jan/2022";
        String expectedOutput = "";
        String output = dateValidationService.validateSprintStartDate(startDate, endDate);

        assertEquals(expectedOutput, output);
    }

    /**
     * Checks that the validateSprintStartDate method returns an error message when given a start date that occurs
     * after the end date.
     */
    @Test
    public void givenInvalidBoundaryDates_whenValidateSprintStartDate_thenOutputWithMessage() {
        String startDate = "02/Jan/2022";
        String endDate = "01/Jan/2022";
        String output = dateValidationService.validateSprintStartDate(startDate, endDate);

        assertTrue(output.length() > 0);
    }

    /**
     * Checks that the getTimeDifferenceFromNow method returns a negative value when given a future date.
     */
    @Test
    public void givenFutureDate_whenGetDifference_thenReturnNegativeTime() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, 5);

        Date future = c.getTime();

        long difference = dateValidationService.getDaysFromNow(future);

        assertTrue(difference < 0);
    }

    /**
     * Checks that the getTimeDifferenceFromNow method returns a positive value when given a past date.
     */
    @Test
    public void givenPastDate_whenGetDifference_thenReturnPositiveTime() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -5);

        Date past = c.getTime();

        long difference = dateValidationService.getDaysFromNow(past);

        assertTrue(difference > 0);
    }

}
