package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

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
}