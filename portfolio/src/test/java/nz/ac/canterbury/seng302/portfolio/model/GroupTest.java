package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for Group class.
 */
class GroupTest {

    /**
     * Tests that the constructor for the Group class sets each variable correctly.
     */
    @Test
    void testConstructor() {
        int expectedGroupId = 0;
        String expectedShortName = "Teaching Staff";
        String expectedLongName = "Users Without Group";
        int expectedCourseId = 3;
        Set<Integer> expectedMemberIds = new HashSet<>();
        Group group = new Group(expectedShortName, expectedLongName, expectedCourseId);

        assertEquals(expectedGroupId, group.getGroupId());
        assertEquals(expectedShortName, group.getShortName());
        assertEquals(expectedLongName, group.getLongName());
        assertEquals(expectedCourseId, group.getCourseId());
        assertEquals(expectedMemberIds, group.getMemberIds());
    }
}
