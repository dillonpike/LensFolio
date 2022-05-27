package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class SprintServiceTest {
    @Mock
    private SprintRepository sprintRepository;

    @InjectMocks
    private SprintService sprintService;


    @Test
    void addSprint_success() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");

        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);

        Sprint created = sprintService.addSprint(sprint);
        assertThat(created.getName()).isSameAs(sprint.getName());
    }

    @Test
    void testGetAllSprint_givenSpringExistInDb_returnNonEmptyList() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        //providing mock/knowledge
        when(sprintRepository.findAll()).thenReturn(sprintList);

        List<Sprint> fetchedSprints = sprintService.getAllSprints();
        assertThat(fetchedSprints.size()).isGreaterThan(0);
        assertThat(fetchedSprints).isSameAs(sprintList);
    }

    @Test
    void testUpdateSprint() {
        Date date = new Date();
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setDescription("This is onl testing");
        sprint.setStartDate(date);
        sprint.setEndDate(date);

        //providing mock/knowledge
        when(sprintRepository.findById(any(Integer.class))).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);

        Sprint updated = sprintService.updateSprint(sprint);
        assertThat(updated.getName()).isSameAs(sprint.getName());
    }

    @Test
    void testUpdateSprintDatesSprintExists() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-21", true));

        //providing mock/knowledge
        when(sprintRepository.findById(any(Integer.class))).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);

        Sprint updated = sprintService.updateSprint(sprint);
        assertThat(updated.getName()).isSameAs(sprint.getName());
    }

    @Test
    void testUpdateSprintDatesSprintDoesNotExist() {
        //providing mock/knowledge
        when(sprintRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        boolean isSuccess = sprintService.updateSprintDates(1, "", "");
        assertFalse(isSuccess);
    }
}