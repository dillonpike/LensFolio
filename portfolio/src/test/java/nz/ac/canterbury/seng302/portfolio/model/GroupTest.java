package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for Group class.
 */
public class GroupTest {

    /**
     * Tests that the constructor for the Group class sets each variable correctly.
     */
    @Test
    void testConstructor() {
        int expectedGroupId = 5;
        String expectedShortName = "Teaching Staff";
        String expectedLongName = "Users Without Group";
        int expectedCourseId = 3;
        Group group = new Group(expectedGroupId, expectedShortName, expectedLongName, expectedCourseId);

        assertEquals(expectedGroupId, group.getGroupId());
        assertEquals(expectedShortName, group.getShortName());
        assertEquals(expectedLongName, group.getLongName());
        assertEquals(expectedCourseId, group.getCourseId());
    }
}
