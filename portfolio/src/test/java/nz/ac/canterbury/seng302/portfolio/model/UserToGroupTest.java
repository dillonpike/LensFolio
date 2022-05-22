package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for UserToGroup class.
 */
public class UserToGroupTest {

    /**
     * Tests that the constructor for the UserToGroup class sets each variable correctly.
     */
    @Test
    void testConstructor() {
        int expectedUserId = 2;
        int expectedGroupId = 4;
        UserToGroup userToGroup = new UserToGroup(expectedUserId, expectedGroupId);

        assertEquals(expectedUserId, userToGroup.getId().getUserId());
        assertEquals(expectedGroupId, userToGroup.getId().getGroupId());
    }
}
