package nz.ac.canterbury.seng302.portfolio.utility;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilityTest {

    /**
     * Tests that the stringToDateTime() method can convert dates written in the expected format.
     */
    @Test
    void validTestStringToDateTime() {
        String dateTime = "12/Dec/2021 4:32 pm";
        Calendar cal = Calendar.getInstance();
        cal.set(2021, Calendar.DECEMBER, 12, 16, 32, 0);
        Date expectedDateTime = cal.getTime();
        Date actualDateTime = DateUtility.stringToDateTime(dateTime);

        // Had to compare as strings since the different date objects weren't considered equal
        assertEquals(expectedDateTime.toString(), actualDateTime.toString());
    }

    /**
     * Tests that the stringToDateTime() method returns null when given a date that doens't follow the expected format.
     */
    @Test
    void invalidTestStringToDateTime() {
        String dateTime = "12/Dece/2021 4:32 pm";
        assertNull(DateUtility.stringToDateTime(dateTime));
    }

    @Test
    void testSetToEndOfDay() {
        Calendar cal = Calendar.getInstance();

        cal.set(2021, Calendar.DECEMBER, 12, 16, 32, 0);
        Date date = cal.getTime();

        cal.set(2021, Calendar.DECEMBER, 12, 23, 59, 59);
        Date expectedDate = cal.getTime();

        assertEquals(DateUtility.setToEndOfDay(date).toString(), expectedDate.toString());
    }
}