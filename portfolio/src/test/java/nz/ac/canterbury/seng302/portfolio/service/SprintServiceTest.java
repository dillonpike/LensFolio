package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class SprintServiceTest {
    @Mock
    private SprintRepository sprintRepository;

    @InjectMocks
    private SprintService sprintService;

    private ArrayList<String> colours;

    @BeforeEach
    public void init() {
        colours = new ArrayList<>(Arrays.asList("#5897fc", "#a758fc", "#fc58c3", "#9e1212", "#c65102", "#d5b60a", "#004400", " #11887b"));
    }


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
        assertThat(fetchedSprints).isNotEmpty().isSameAs(sprintList);
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

    /**
     * Tests that if the updateSprintDates method is given the id of a sprint that exists, it updates the sprint with
     * the given dates.
     */
    @Test
    void testUpdateSprintDatesSprintExists() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-22", true));

        //providing mock/knowledge
        when(sprintRepository.findById(any(Integer.class))).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);

        String newStartDate = "2001-12-25";
        String newEndDate = "2001-12-27";

        boolean isSuccess = sprintService.updateSprintDates(sprint.getId(), newStartDate, newEndDate);
        assertTrue(isSuccess);
        assertEquals(sprint.getStartDate(), sprintService.calendarDateStringToDate(newStartDate, false));
        assertEquals(sprint.getEndDate(), sprintService.calendarDateStringToDate(newEndDate, true));
    }

    /**
     * Tests that if the updateSprintDates method is given the id of a sprint that does not exist, it returns false.
     */
    @Test
    void testUpdateSprintDatesSprintDoesNotExist() {
        //providing mock/knowledge
        when(sprintRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        boolean isSuccess = sprintService.updateSprintDates(1, "", "");
        assertFalse(isSuccess);
    }

    /**
     * Tests that the calendarDateStringToDate method returns null if given a date string in an invalid format.
     */
    @Test
    void testCalendarDateStringToDateInvalid() {
        Date date = sprintService.calendarDateStringToDate("20/Jan/2001", true);
        assertNull(date);
    }

    @Test
    void givenOneSprint_returnFirstColour() {
        Sprint sprint = new Sprint();
        sprint.setName("Test");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);
        when(sprintRepository.findAllByOrderBySprintStartDate()).thenReturn(sprintList);

        List<Sprint> outputSprints = sprintService.getAllSprintsOrderedWithColour();
        assertThat(outputSprints.get(0).getColour()).isSameAs(colours.get(0));
    }

    @Test
    void givenMultipleSprints_returnSprintsWithColorInOrder() {
        Sprint sprint1 = new Sprint();
        Sprint sprint2 = new Sprint();
        Sprint sprint3 = new Sprint();
        Sprint sprint4 = new Sprint();
        Sprint sprint5 = new Sprint();
        Sprint sprint6 = new Sprint();
        Sprint sprint7 = new Sprint();
        Sprint sprint8 = new Sprint();

        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint1);
        sprintList.add(sprint2);
        sprintList.add(sprint3);
        sprintList.add(sprint4);
        sprintList.add(sprint5);
        sprintList.add(sprint6);
        sprintList.add(sprint7);
        sprintList.add(sprint8);

        when(sprintRepository.findAllByOrderBySprintStartDate()).thenReturn(sprintList);
        List<Sprint> outputSprints = sprintService.getAllSprintsOrderedWithColour();

        assertThat(outputSprints.get(0).getColour()).isSameAs(colours.get(0));
        assertThat(outputSprints.get(1).getColour()).isSameAs(colours.get(1));
        assertThat(outputSprints.get(2).getColour()).isSameAs(colours.get(2));
        assertThat(outputSprints.get(3).getColour()).isSameAs(colours.get(3));
        assertThat(outputSprints.get(4).getColour()).isSameAs(colours.get(4));
        assertThat(outputSprints.get(5).getColour()).isSameAs(colours.get(5));
        assertThat(outputSprints.get(6).getColour()).isSameAs(colours.get(6));
        assertThat(outputSprints.get(7).getColour()).isSameAs(colours.get(7));


    }
}