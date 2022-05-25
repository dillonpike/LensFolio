package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for UserSorting class.
 */
class UserSortingTest {

    /**
     * Tests that the 'full' constructor for the UserSorting class (one that accepts three variables) sets each
     * variable correctly.
     */
    @Test
    void testFullConstructor() {
        int expectedUserId = 5;
        int expectedColumnIndex = 3;
        String expectedSortOrder = "desc";
        UserSorting userSorting = new UserSorting(expectedUserId, expectedColumnIndex, expectedSortOrder);

        assertEquals(expectedUserId, userSorting.getUserId());
        assertEquals(expectedColumnIndex, userSorting.getColumnIndex());
        assertEquals(expectedSortOrder, userSorting.getSortOrder());
    }

    /**
     * Tests that the constructor for the UserSorting class that only accepts a user id sets the id, column index, and
     * sort order correctly.
     */
    @Test
    void testUserIDConstructor() {
        int expectedUserId = 3;
        int expectedColumnIndex = 0;
        String expectedSortOrder = "asc";
        UserSorting userSorting = new UserSorting(expectedUserId);

        assertEquals(expectedUserId, userSorting.getUserId());
        assertEquals(expectedColumnIndex, userSorting.getColumnIndex());
        assertEquals(expectedSortOrder, userSorting.getSortOrder());
    }
}
