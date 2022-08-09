package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Event class.
 */
class EventTest {

    /**
     * Tests that the constructor for the Event class sets each variable correctly.
     */
    @Test
    void testConstructor() {
        int expectedParentProjectId = 0;
        String expectedEventName = "Test Event";
        Date expectedEventStartDate = new Date();
        Date expectedEventEndDate = new Date();
        LocalTime expectedEventStartTime = LocalTime.of(12, 12, 12);
        LocalTime expectedEventEndTime = LocalTime.of(17, 13, 8);

        Event event = new Event(expectedParentProjectId, expectedEventName, expectedEventStartDate,
                expectedEventEndDate, expectedEventStartTime, expectedEventEndTime);

        assertEquals(expectedParentProjectId, event.getParentProjectId());
        assertEquals(expectedEventName, event.getEventName());
        assertEquals(expectedEventStartDate, event.getEventStartDate());
        assertEquals(expectedEventEndDate, event.getEventEndDate());
        assertEquals(expectedEventStartTime, event.getEventStartTime());
        assertEquals(expectedEventEndTime, event.getEventEndTime());
    }

    /**
     * Tests that the setting and getting the start date as a detailed formatted string works.
     * @throws ParseException when the set method fails to parse the date, which fails the test
     */
//    @Test
//    void testStartDateDetail() throws ParseException {
//        Event event = new Event(0, "Test Event", new Date(), new Date(),
//                LocalTime.of(12, 12, 12), LocalTime.of(17, 13, 8));
//        String expectedStartDateDetail = "12/Jun/2012 3:45 pm";
//        event.setStartDateDetail(expectedStartDateDetail);
//
//        assertEquals(expectedStartDateDetail, event.getStartDateDetail());
//    }

    /**
     * Tests that the setting and getting the end date as a detailed formatted string works.
     * @throws ParseException when the set method fails to parse the date, which fails the test
     */
//    @Test
//    void testEndDateDetail() throws ParseException {
//        Event event = new Event(0, "Test Event", new Date(), new Date(),
//                LocalTime.of(12, 12, 12), LocalTime.of(17, 13, 8));
//        String expectedEndDateDetail = "12/Jun/2012 3:45 pm";
//        event.setEndDateDetail(expectedEndDateDetail);
//
//        assertEquals(expectedEndDateDetail, event.getEndDateDetail());
//    }
}