package nz.ac.canterbury.seng302.portfolio.service;

import java.util.ArrayList;
import java.util.List;
import io.cucumber.java.hu.De;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.DeadlinesRepository;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GroupService class.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class DeadlineServiceTest {
    /**
     * Mocked repository of Deadlines objects.
     */
    @Mock
    private DeadlinesRepository deadlinesRepository;

    /**
     * Mocked repository of DateValidationService objects.
     */
    @Mock
    private DateValidationService dateValidationService;

    @Mock
    private SprintRepository sprintRepository;


    /**
     * DeadlineService object.
     */
    @InjectMocks
    private DeadlineService deadlineService;

    @InjectMocks
    private SprintService sprintService;

    /**
     * test UpdateDeadline method in DeadlineService class given the deadline exist in database
     * This test verifies whether the method call appropriate method to persist data to database
     * Expect it return deadlines object with update data
     */
    @Test
    void updateDeadlineWhenTheDeadlineExistInDatabase() {
        Deadline paramDeadline = new Deadline(0,"newTest",new Date(100));
        Deadline deadline = new Deadline(0 ,"oldName", new Date(10));
        Optional<Deadline> sOptional = Optional.of(deadline);

        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);
        when(deadlinesRepository.save(any(Deadline.class))).thenReturn(paramDeadline);

        Deadline actual = deadlineService.updateDeadline(paramDeadline);

        assertEquals("newTest",actual.getDeadlineName());
        assertEquals(new Date(100),actual.getDeadlineDate());
        ArgumentCaptor<Deadline> deadlinesArgumentCaptor = ArgumentCaptor.forClass(Deadline.class);
        verify(deadlinesRepository).save(deadlinesArgumentCaptor.capture());
    }

    /**
     * test UpdateDeadline method in DeadlineService class given the deadline does not exist in database
     * Expect it return deadlines object with same data with the deadline object pass to this method
     */
    @Test
    void updateDeadlineWhenTheDeadlineNotExistInDatabase() {
        Deadline paramDeadline = new Deadline(0,"deadline not exist in database",new Date(10));

        Optional<Deadline> sOptional = Optional.empty();

        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);

        Deadline actual = deadlineService.updateDeadline(paramDeadline);

        assertEquals("deadline not exist in database",actual.getDeadlineName());
        assertEquals(new Date(10),actual.getDeadlineDate());

    }

    /**
     * test getDeadlineById method in DeadlineService class given the deadline exist in database
     * Expect it return correct deadlines object with same id with the given param
     */
    @Test
    void getDeadlineByIdWhenDeadlineExist() throws Exception {
        Deadline deadline = new Deadline(0 ,"oldName", new Date(10));
        Optional<Deadline> sOptional = Optional.of(deadline);
        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);
        Deadline actual = deadlineService.getDeadlineById(1);

        assertEquals(deadline, actual);


    }

    /**
     * test getDeadlineById method in DeadlineService class given the deadline does not exist in database
     * Expect it throws Exception with message 'Event not found'
     */
    @Test
    void getDeadlineByIdWhenDeadlineDoesNotExist() {
        Exception exception = assertThrows(Exception.class, () -> {
            Optional<Deadline> sOptional = Optional.empty();
            when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);
            deadlineService.getDeadlineById(1);
        });
        System.out.println(exception.getMessage());
        String expectedMessage = "Unknown deadline#1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the addDeadline method calls repository.save() to save the given deadline to the database, then
     * returns the saved deadline.
     */
    @Test
    void testAddDeadline() {
        when(deadlinesRepository.save(any(Deadline.class))).then(returnsFirstArg());
        Deadline expectedDeadline = new Deadline(0,"Test Deadline", new Date());
        Deadline deadline = deadlineService.addDeadline(expectedDeadline);

        verify(deadlinesRepository, times(1)).save(expectedDeadline);
        assertEquals(expectedDeadline, deadline);
    }

    /**
     * Tests that the testRemove method calls the repository's deleteById method with the expected deadline id when a
     * deadline with the given id exists.
     */
    @Test
    void testRemoveDeadlineExists() {
        Deadline testDeadline = new Deadline(0, "Test Deadline", new Date());
        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(Optional.of(testDeadline));
        deadlineService.removeDeadline(testDeadline.getId());

        verify(deadlinesRepository, times(1)).deleteById(testDeadline.getId());

    }

    /**
     * Tests that the testRemove method does not call the repository's deleteById method with when a deadline with the
     * given id does not exist.
     */
    @Test
    void testRemoveMilestoneDoesNotExist() {
        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        deadlineService.removeDeadline(1);

        verify(deadlinesRepository, times(0)).deleteById(any(Integer.class));
    }

    @Test
    void testValidateDeadlineDateInSprintDateRange_givenDeadlineInSprintDateRange_returnTrue() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-29", true));

        Deadline deadline = new Deadline();

        deadline.setDeadlineName("Date In Sprint Range");
        deadline.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-21", false));

        boolean expectResult = deadlineService.validateDeadlineDateInSprintDateRange(deadline, sprint);

        assertTrue(expectResult);
    }

    @Test
    void testValidateDeadlineDateInSprintDateRange_givenDeadlineNotInSprintDateRange_returnFalse() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-29", true));

        Deadline deadline = new Deadline();

        deadline.setDeadlineName("Date In Sprint Range");
        deadline.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-19", false));

        boolean expectResult = deadlineService.validateDeadlineDateInSprintDateRange(deadline, sprint);

        assertFalse(expectResult);
    }

    @Test
    void testValidateDeadlineDateInSprintDateRange_givenDeadlineDateInSprintStartDay_returnTrue() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-29", true));

        Deadline deadline = new Deadline();

        deadline.setDeadlineName("Date In Sprint Range");
        deadline.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-20", false));

        boolean expectResult = deadlineService.validateDeadlineDateInSprintDateRange(deadline, sprint);

        assertTrue(expectResult);
    }

    @Test
    void testValidateDeadlineDateInSprintDateRange_givenDeadlineDateInSprintEndDay_returnTrue() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-29", true));

        Deadline deadline = new Deadline();

        deadline.setDeadlineName("Date In Sprint Range");
        deadline.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-29", true));

        boolean expectResult = deadlineService.validateDeadlineDateInSprintDateRange(deadline, sprint);

        assertTrue(expectResult);
    }

    @Test
    void givenOneSprint_returnDeadlinesThatOverlap() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-29", true));
        sprint.setColour("#5897fc");

        Deadline deadline1 = new Deadline();
        Deadline deadline2 = new Deadline();
        Deadline deadline3 = new Deadline();
        Deadline deadline4 = new Deadline();

        deadline1.setDeadlineName("Date Overlaps");
        deadline1.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-21", false));

        deadline2.setDeadlineName("Date not in sprint");
        deadline2.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-19", false));

        deadline3.setDeadlineName("No Dates Overlap");
        deadline3.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-23", false));

        deadline4.setDeadlineName("Sprint start date");
        deadline4.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-20", false));

        List<Deadline> deadlineList = new ArrayList<>();
        deadlineList.add(deadline1);
        deadlineList.add(deadline2);
        deadlineList.add(deadline3);
        deadlineList.add(deadline4);

        when(deadlineService.getAllDeadlinesOrdered()).thenReturn(deadlineList);

        List<Deadline> returnedDeadlines = deadlineService.getAllDeadlinesOverLappingWithSprint(sprint);

        assertEquals(deadline1.getDeadlineName(), returnedDeadlines.get(0).getDeadlineName());
        assertEquals(deadline3.getDeadlineName(), returnedDeadlines.get(1).getDeadlineName());
        assertEquals(deadline4.getDeadlineName(), returnedDeadlines.get(2).getDeadlineName());

    }

    /**
     * Tests that the getAllDeadlinesOrderedWithColour method returns the correct colour given
     * comfortable date range.
     */
    @Test
    void givenOneDeadlineExistAndDateInRange_returnBlueColor() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-25", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Deadline deadline = new Deadline();
        deadline.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-23", false));
        deadline.setDeadlineName("Test Deadline");
        List<Deadline> deadlineList = new ArrayList<>();
        deadlineList.add(deadline);

        when(deadlineService.getAllDeadlinesOrderedWithColour(sprintList)).thenReturn(deadlineList);

        List<Deadline> outputDeadlineList = deadlineService.getAllDeadlinesOrderedWithColour(sprintList);
        assertThat(outputDeadlineList.size()).isSameAs(deadlineList.size());
        assertThat(outputDeadlineList.get(0).getDeadlineColour()).isSameAs(sprint.getColour());
    }

    /**
     * Tests that the getAllDeadlinesOrderedWithColour method returns the correct colour given
     * the deadline is the same day as the sprint start date.
     */
    @Test
    void givenOneDeadlineExistAndDateIsTheSameAsStart_returnBlueColor() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-25", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Deadline deadline = new Deadline();
        deadline.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        deadline.setDeadlineName("Test Deadline");
        List<Deadline> deadlineList = new ArrayList<>();
        deadlineList.add(deadline);

        when(deadlineService.getAllDeadlinesOrderedWithColour(sprintList)).thenReturn(deadlineList);

        List<Deadline> outputDeadlineList = deadlineService.getAllDeadlinesOrderedWithColour(sprintList);
        assertThat(outputDeadlineList.size()).isSameAs(deadlineList.size());
        assertThat(outputDeadlineList.get(0).getDeadlineColour()).isSameAs(sprint.getColour());
    }

    /**
     * Tests that the getAllDeadlinesOrderedWithColour method returns the correct colour given
     * the deadline is the same day as the sprint end date.
     */
    @Test
    void givenOneDeadlineExistAndDateIsTheSameAsEnd_returnNotBlueColor() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-25", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Deadline deadline = new Deadline();
        deadline.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-25", false));
        deadline.setDeadlineName("Test Deadline");
        List<Deadline> deadlineList = new ArrayList<>();
        deadlineList.add(deadline);

        when(deadlineService.getAllDeadlinesOrderedWithColour(sprintList)).thenReturn(deadlineList);

        List<Deadline> outputDeadlineList = deadlineService.getAllDeadlinesOrderedWithColour(sprintList);
        assertThat(outputDeadlineList.size()).isSameAs(deadlineList.size());
        assertThat(outputDeadlineList.get(0).getDeadlineColour()).isNotSameAs(sprint.getColour());
    }

    /**
     * Tests that the getAllDeadlinesOrderedWithColour method returns the correct colour given
     * the deadline is the same day as the sprint start date.
     */
    @Test
    void givenOneDeadlineExistAndDateIsOneDayAfterEnd_returnNotBlueColor() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-25", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Deadline deadline = new Deadline();
        deadline.setDeadlineDate(sprintService.calendarDateStringToDate("2001-12-26", false));
        deadline.setDeadlineName("Test Deadline");
        List<Deadline> deadlineList = new ArrayList<>();
        deadlineList.add(deadline);

        when(deadlineService.getAllDeadlinesOrderedWithColour(sprintList)).thenReturn(deadlineList);

        List<Deadline> outputDeadlineList = deadlineService.getAllDeadlinesOrderedWithColour(sprintList);
        assertThat(outputDeadlineList.size()).isSameAs(deadlineList.size());
        assertThat(outputDeadlineList.get(0).getDeadlineColour()).isNotSameAs(sprint.getColour());
    }

}