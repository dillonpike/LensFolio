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

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class DeadlinesServiceTest {
    @Mock
    private DeadlinesRepository deadlinesRepository;

    @InjectMocks
    private DeadlinesService deadlinesService;

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

    @Test
    void updateDeadlineWhenTheDeadlineNotExistInDatabase() {
        Deadlines paramDeadline = new Deadlines(1,0,"deadline not exist in database",new Date(10));

        Optional<Deadlines> sOptional = Optional.empty();

        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);

        Deadlines actual = deadlinesService.updateDeadline(paramDeadline);

        Assertions.assertEquals("deadline not exist in database",actual.getDeadlineName());
        Assertions.assertEquals(new Date(10),actual.getDeadlineDate());

    }

    @Test
    void getEventByIdWhenDeadlineExist() throws Exception {
        Deadlines deadlines = new Deadlines(1 ,0 ,"oldName", new Date(10));
        Optional<Deadlines> sOptional = Optional.of(deadlines);
        when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);
        Deadlines actual = deadlinesService.getEventById(1);

        Assertions.assertEquals(deadlines, actual);


    }

    @Test
    void getEventByIdWhenDeadlineDoesNotExist() {
        Exception exception = assertThrows(Exception.class, () -> {
            Optional<Deadlines> sOptional = Optional.empty();
            when(deadlinesRepository.findById(any(Integer.class))).thenReturn(sOptional);
            deadlinesService.getEventById(1);
        });

        String expectedMessage = "Event not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}