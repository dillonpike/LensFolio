package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;


/**
 * Unit tests for the Milestone model class.
 */
class MilestoneTest {
    private String expectedMilestoneName = "Test Milestone";
    private Date expectedMilestoneDate = new Date();

    /**
     * Tests that the constructor for the Milestone class sets each variable correctly.
     */
    @Test
    void testConstructor() {
        int expectedParentProjectId = 0;
        Milestone milestone = new Milestone(expectedParentProjectId, expectedMilestoneName, expectedMilestoneDate);

        assertEquals(expectedParentProjectId, milestone.getParentProjectId());
        assertEquals(expectedMilestoneName, milestone.getMilestoneName());
        assertEquals(expectedMilestoneDate, milestone.getMilestoneDate());
    }

    /**
     * Tests that the toString method displays all the attributes correctly.
     */
    @Test
    void testToString() {
        int expectedParentProjectId = 0;
        String expectedString = "milestone[id=0, parentProjectId='" + expectedParentProjectId + "', milestoneName='" +
                expectedMilestoneName + "', milestoneDate='" + expectedMilestoneDate + "']";
        Milestone milestone = new Milestone(expectedParentProjectId, expectedMilestoneName, expectedMilestoneDate);

        assertEquals(expectedString, milestone.toString());
    }
}