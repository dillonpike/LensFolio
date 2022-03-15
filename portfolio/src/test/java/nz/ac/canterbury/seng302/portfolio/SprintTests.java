package nz.ac.canterbury.seng302.portfolio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.Project;

/**
 * Unit tests for sprint class.
 */
class SprintTests {

    /**
     * Test sprint object.
     */
    private static Sprint sprint;

    /**
     * Test sprint label number.
     */
    private static int labelNum = 1;

    /**
     * Test sprint name.
     */
    private static String sprintName = "Sprint 1";

    /**
     * Test sprint description.
     */
    private static String sprintDesc = "desc";

    /**
     * Test sprint start date.
     */
    private static LocalDate sprintStartDate = LocalDate.of(2022, 3, 5);

    /**
     * Test sprint end date.
     */
    private static LocalDate sprintEndDate = LocalDate.of(2022, 8, 5);

    /**
     * End date that's on the boundary while still being valid, i.e. one day after the start date.
     */
    private static LocalDate sprintEndDateValidBoundary = sprintStartDate.plusDays(1);

    /**
     * Invalid end date that is before the start date.
     */
    private static LocalDate sprintEndDateInvalid = sprintStartDate.minusDays(15).minusMonths(3);

    /**
     * Test sprint project name.
     */
    private static String projectName = "Project 1";


    /**
     * Sets up a valid sprint.
     */
    @BeforeAll
    static void createValidSprint() {
        try {
            sprint = new Sprint(labelNum, sprintName, sprintDesc, sprintStartDate, sprintEndDate, projectName);
        } catch (Exception e) {
            fail("Valid sprint failed to be created.\nException: " + e);
        }
    }

    /**
     * Checks the label number has been set correctly.
     */
    @Test
    void checkLabelNum() {
        assertEquals(labelNum, sprint.getLabelNum());
    }

    /**
     * Checks the name has been set correctly.
     */
    @Test
    void checkName() {
        assertEquals(sprintName, sprint.getName());
    }

    /**
     * Checks the description has been set correctly.
     */
    @Test
    void checkDescription() {
        assertEquals(sprintDesc, sprint.getDescription());
    }

    /**
     * Checks the start date has been set correctly.
     */
    @Test
    void checkStartDate() {
        assertEquals(sprintStartDate, sprint.getStartDate());
    }

    /**
     * Checks the end date has been set correctly.
     */
    @Test
    void checkEndDate() {
        assertEquals(sprintEndDate, sprint.getEndDate());
    }

    /**
     * Checks the project name has been set correctly.
     */
    @Test
    void checkProjectName() {
        assertEquals(projectName, sprint.getProjectName());
    }

    /**
     * Checks that a sprint can be created with the end date as the day after the start date (valid boundary).
     */
    @Test
    void endDateValidBoundaryWithConstructor() {
        Sprint testSprint = null;
        try {
            testSprint = new Sprint(labelNum, sprintName, sprintDesc, sprintStartDate, sprintEndDateValidBoundary,
                                    projectName);
        } catch (Exception e) {
            fail("Couldn't create valid sprint with end date on the day after the start date (boundary)." +
                    "\nException: " + e);
        }
    }

    /**
     * Checks that a sprint cannot be created with an equal start date and end date.
     */
    @Test
    void endDateInvalidBoundaryWithConstructor() {
        Sprint testSprint = null;
        try {
            testSprint = new Sprint(labelNum, sprintName, sprintDesc, sprintStartDate, sprintStartDate, projectName);
            fail("Should not be able to create a sprint with the same start and end date.");
        } catch (Exception e) {
            assertEquals(testSprint, null);
        }
    }

    /**
     * Checks that a sprint cannot be created with an end date that is earlier than the start date.
     */
    @Test
    void endDateInvalidWithConstructor() {
        Sprint testSprint = null;
        try {
            testSprint = new Sprint(labelNum, sprintName, sprintDesc, sprintStartDate, sprintEndDateInvalid,
                                    projectName);
            fail("Should not be able to create a sprint with the end date before the start date.");
        } catch (Exception e) {
            assertEquals(testSprint, null);
        }
    }

    /**
     * Checks that the end date can be set to a day after the start date after the sprint has been created.
     */
    @Test
    void endDateBoundaryWithSetter() {
        try {
            sprint.setEndDate(sprintEndDateValidBoundary);
        } catch (Exception e) {
            fail("Should be able to set the end date to the day after the start date.");
        }
        assertEquals(sprintEndDateValidBoundary, sprint.getEndDate());
    }

    /**
     * Checks that the end date cannot be set as the start date after the sprint has been created.
     */
    @Test
    void endDateInvalidBoundaryWithSetter() {
        LocalDate endDate = sprint.getEndDate();
        try {
            sprint.setEndDate(sprint.getStartDate());
            fail("Should not be able to set the end date as the start date.");
        } catch (Exception e) {
            assertEquals(endDate, sprint.getEndDate());
        }
    }

    /**
     * Checks that the end date cannot be set to a date earlier than the start date after the sprint has been created.
     */
    @Test
    void endDateInvalidWithSetter() {
        LocalDate endDate = sprint.getEndDate();
        try {
            sprint.setEndDate(sprintEndDateInvalid);
            fail("Should not be able to set the end date as a date before the start date.");
        } catch (Exception e) {
            assertEquals(endDate, sprint.getEndDate());
        }
    }

    /**
     * Checks that the start date can be set to a one day earlier than the end date after the sprint has been created.
     */
    @Test
    void startDateValidBoundaryWithSetter() {
        LocalDate startDate = sprint.getEndDate().minusDays(1);
        try {
            sprint.setStartDate(startDate);
        } catch (Exception e) {
            fail("Should be able to set the start date as the day before the end date.");
        }
        assertEquals(startDate, sprint.getStartDate());
    }

    /**
     * Checks that the start date cannot be set to the end date after the sprint has been created.
     */
    @Test
    void startDateInvalidBoundaryWithSetter() {
        LocalDate startDate = sprint.getEndDate();
        try {
            sprint.setStartDate(startDate);
            fail("Should not be able to set a start date as the end date.");
        } catch (Exception e) {
            assertNotEquals(startDate, sprint.getStartDate());
        }
    }

    /**
     * Checks that the start date cannot be set to a date after the end date after the sprint has been created.
     */
    @Test
    void startDateInvalidWithSetter() {
        LocalDate startDate = sprint.getEndDate().plusDays(10).plusMonths(3);
        try {
            sprint.setStartDate(startDate);
            fail("Should not be able to set a start date that's after the end date.");
        } catch (Exception e) {
            assertNotEquals(startDate, sprint.getStartDate());
        }
    }
}
