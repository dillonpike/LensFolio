package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Deadlines;
import nz.ac.canterbury.seng302.portfolio.repository.DeadlinesRepository;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GroupService class.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class DeadlinesServiceTest {
    /**
     * Mocked repository of Deadlines objects.
     */
    @Mock
    private DeadlinesRepository deadlinesRepository;

    /**
     * DeadlineService object.
     */
    @InjectMocks
    private DeadlinesService deadlinesService;

    /**
     * test UpdateDeadline method in DeadlineService class given the deadline exist in database
     * This test verifies whether the method call appropriate method to persist data to database
     * Expect it return deadlines object with update data
     */
    @Test
    void updateDeadlineWhenTheDeadlineExistInDatabase() {
        Deadlines paramDeadline = new Deadlines(1,0,"newTest",new Date(100));
        Deadlines deadlines = new Deadlines(1 ,0 ,"oldName", new Date(10));
        Optional<Deadlines> sOptional = Optional.of(deadlines);

        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);
        when(deadlinesRepository.save(any(Deadlines.class))).thenReturn(paramDeadline);

        Deadlines actual = deadlinesService.updateDeadline(paramDeadline);

        Assertions.assertEquals("newTest",actual.getDeadlineName());
        Assertions.assertEquals(new Date(100),actual.getDeadlineDate());
        ArgumentCaptor<Deadlines> deadlinesArgumentCaptor = ArgumentCaptor.forClass(Deadlines.class);
        Mockito.verify(deadlinesRepository).save(deadlinesArgumentCaptor.capture());
    }

    /**
     * test UpdateDeadline method in DeadlineService class given the deadline does not exist in database
     * Expect it return deadlines object with same data with the deadline object pass to this method
     */
    @Test
    void updateDeadlineWhenTheDeadlineNotExistInDatabase() {
        Deadlines paramDeadline = new Deadlines(1,0,"deadline not exist in database",new Date(10));

        Optional<Deadlines> sOptional = Optional.empty();

        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);

        Deadlines actual = deadlinesService.updateDeadline(paramDeadline);

        Assertions.assertEquals("deadline not exist in database",actual.getDeadlineName());
        Assertions.assertEquals(new Date(10),actual.getDeadlineDate());

    }

    /**
     * test getDeadlineById method in DeadlineService class given the deadline exist in database
     * Expect it return correct deadlines object with same id with the given param
     */
    @Test
    void getDeadlineByIdWhenDeadlineExist() throws Exception {
        Deadlines deadlines = new Deadlines(1 ,0 ,"oldName", new Date(10));
        Optional<Deadlines> sOptional = Optional.of(deadlines);
        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);
        Deadlines actual = deadlinesService.getDeadlineById(1);

        Assertions.assertEquals(deadlines, actual);


    }

    /**
     * test getDeadlineById method in DeadlineService class given the deadline does not exist in database
     * Expect it throws Exception with message 'Event not found'
     */
    @Test
    void getEventByIdWhenDeadlineDoesNotExist() {
        Exception exception = assertThrows(Exception.class, () -> {
            Optional<Deadlines> sOptional = Optional.empty();
            when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);
            deadlinesService.getDeadlineById(1);
        });

        String expectedMessage = "Event not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}