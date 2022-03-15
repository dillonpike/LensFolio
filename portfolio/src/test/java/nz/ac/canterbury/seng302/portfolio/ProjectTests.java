package nz.ac.canterbury.seng302.portfolio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import nz.ac.canterbury.seng302.portfolio.model.Project;

/**
 * Unit tests for project class.
 */
class ProjectTests {

    /**
     * Test project object.
     */
    private static Project project;

    /**
     * Test project name.
     */
    private static String projectName = "Project 1";

    /**
     * Test project description.
     */
    private static String projectDesc = "desc";

    /**
     * Test project start date.
     */
    private static LocalDate projectStartDate = LocalDate.of(2022, 3, 5);

    /**
     * Test project end date.
     */
    private static LocalDate projectEndDate = LocalDate.of(2022, 8, 5);

    /**
     * End date that's on the boundary while still being valid, i.e. one day after the start date.
     */
    private static LocalDate projectEndDateValidBoundary = projectStartDate.plusDays(1);

    /**
     * Invalid end date that is before the start date.
     */
    private static LocalDate projectEndDateInvalid = projectStartDate.minusDays(15).minusMonths(3);

    /**
     * Sets up a valid project.
     */
    @BeforeAll
    static void createValidProject() {
        try {
            project = new Project(projectName, projectDesc, projectStartDate, projectEndDate);
        } catch (Exception e) {
            fail("Valid project failed to be created.\nException: " + e);
        }
    }

    /**
     * Checks the name has been set correctly.
     */
    @Test
    void checkName() {
        assertEquals(projectName, project.getName());
    }

    /**
     * Checks the description has been set correctly.
     */
    @Test
    void checkDescription() {
        assertEquals(projectDesc, project.getDescription());
    }

    /**
     * Checks the start date has been set correctly.
     */
    @Test
    void checkStartDate() {
        assertEquals(projectStartDate, project.getStartDate());
    }

    /**
     * Checks the end date has been set correctly.
     */
    @Test
    void checkEndDate() {
        assertEquals(projectEndDate, project.getEndDate());
    }

    /**
     * Checks that a project can be created with the end date as the day after the start date (valid boundary).
     */
    @Test
    void endDateValidBoundaryWithConstructor() {
        Project testProject = null;
        try {
            testProject = new Project(projectName, projectDesc, projectStartDate, projectEndDateValidBoundary);
        } catch (Exception e) {
            fail("Couldn't create valid project with end date on the day after the start date (boundary)." +
                 "\nException: " + e);
        }
    }

    /**
     * Checks that a project cannot be created with an equal start date and end date.
     */
    @Test
    void endDateInvalidBoundaryWithConstructor() {
        Project testProject = null;
        try {
            testProject = new Project(projectName, projectDesc, projectStartDate, projectStartDate);
            fail("Should not be able to create a project with the same start and end date.");
        } catch (Exception e) {
            assertEquals(testProject, null);
        }
    }

    /**
     * Checks that a project cannot be created with an end date that is earlier than the start date.
     */
    @Test
    void endDateInvalidWithConstructor() {
        Project testProject = null;
        try {
            testProject = new Project(projectName, projectDesc, projectStartDate, projectEndDateInvalid);
            fail("Should not be able to create a project with the end date before the start date.");
        } catch (Exception e) {
            assertEquals(testProject, null);
        }
    }

    /**
     * Checks that the end date can be set to a day after the start date after the project has been created.
     */
    @Test
    void endDateBoundaryWithSetter() {
        try {
            project.setEndDate(projectEndDateValidBoundary);
        } catch (Exception e) {
            fail("Should be able to set the end date to the day after the start date.");
        }
        assertEquals(projectEndDateValidBoundary, project.getEndDate());
    }

    /**
     * Checks that the end date cannot be set as the start date after the project has been created.
     */
    @Test
    void endDateInvalidBoundaryWithSetter() {
        LocalDate endDate = project.getEndDate();
        try {
            project.setEndDate(project.getStartDate());
            fail("Should not be able to set the end date as the start date.");
        } catch (Exception e) {
            assertEquals(endDate, project.getEndDate());
        }
    }

    /**
     * Checks that the end date cannot be set to a date earlier than the start date after the project has been created.
     */
    @Test
    void endDateInvalidWithSetter() {
        LocalDate endDate = project.getEndDate();
        try {
            project.setEndDate(projectEndDateInvalid);
            fail("Should not be able to set the end date as a date before the start date.");
        } catch (Exception e) {
            assertEquals(endDate, project.getEndDate());
        }
    }

    /**
     * Checks that the start date can be set to a one day earlier than the end date after the project has been created.
     */
    @Test
    void startDateValidBoundaryWithSetter() {
        LocalDate startDate = project.getEndDate().minusDays(1);
        try {
            project.setStartDate(startDate);
        } catch (Exception e) {
            fail("Should be able to set the start date as the day before the end date.");
        }
        assertEquals(startDate, project.getStartDate());
    }

    /**
     * Checks that the start date cannot be set to the end date after the project has been created.
     */
    @Test
    void startDateInvalidBoundaryWithSetter() {
        LocalDate startDate = project.getEndDate();
        try {
            project.setStartDate(startDate);
            fail("Should not be able to set a start date as the end date.");
        } catch (Exception e) {
            assertNotEquals(startDate, project.getStartDate());
        }
    }

    /**
     * Checks that the start date cannot be set to a date after the end date after the project has been created.
     */
    @Test
    void startDateInvalidWithSetter() {
        LocalDate startDate = project.getEndDate().plusDays(10).plusMonths(3);
        try {
            project.setStartDate(startDate);
            fail("Should not be able to set a start date that's after the end date.");
        } catch (Exception e) {
            assertNotEquals(startDate, project.getStartDate());
        }
    }
}
