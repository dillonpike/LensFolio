package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Deadline model class.
 */
class DeadlineTest {

    /**
     * Tests that the constructor for the Deadline class sets each variable correctly.
     */
    @Test
    void testConstructor() {
        int expectedParentProjectId = 0;
        String expectedDeadlineName = "Test Deadline";
        Date expectedDeadlineDate = new Date();
        Deadline deadline = new Deadline(expectedParentProjectId, expectedDeadlineName, expectedDeadlineDate);

        assertEquals(expectedParentProjectId, deadline.getParentProjectId());
        assertEquals(expectedDeadlineName, deadline.getDeadlineName());
        assertEquals(expectedDeadlineDate, deadline.getDeadlineDate());
    }

    /**
     * Tests that the toString method displays all the attributes correctly.
     */
    @Test
    void testToString() {
        int expectedParentProjectId = 0;
        String expectedDeadlineName = "Test Deadline";
        Date expectedDeadlineDate = new Date();
        String expectedString = "deadline[id=0, parentProjectId='" + expectedParentProjectId + "', deadlineName='" +
                expectedDeadlineName + "', deadlineDate='" + expectedDeadlineDate + "']";
        Deadline deadline = new Deadline(expectedParentProjectId, expectedDeadlineName, expectedDeadlineDate);

        assertEquals(expectedString, deadline.toString());
    }

    /**
     * Checks that the getDeadlineTimeString12Hour() method returns the expected time in 12-hour format.
     */
    @Test
    void test12HourTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, 05, 27, 22, 15, 35);
        Deadline deadline = new Deadline(0, "Test Deadline", calendar.getTime());
        assertEquals("10:15 pm", deadline.getDeadlineTimeString12Hour());
    }
}