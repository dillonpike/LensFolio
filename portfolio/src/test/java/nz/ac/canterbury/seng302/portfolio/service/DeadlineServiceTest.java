package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.repository.DeadlinesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.Optional;

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
     * DeadlineService object.
     */
    @InjectMocks
    private DeadlineService deadlineService;

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
    void getEventByIdWhenDeadlineDoesNotExist() {
        Exception exception = assertThrows(Exception.class, () -> {
            Optional<Deadline> sOptional = Optional.empty();
            when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);
            deadlineService.getDeadlineById(1);
        });

        String expectedMessage = "Event not found";
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
}