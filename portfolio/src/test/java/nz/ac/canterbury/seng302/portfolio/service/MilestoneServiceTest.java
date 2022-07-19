package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MilestoneService class.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class MilestoneServiceTest {
    /**
     * Mocked repository of Milestone objects.
     */
    @Mock
    private MilestoneRepository milestoneRepository;

    /**
     * DeadlineService object.
     */
    @InjectMocks
    private MilestoneService milestoneService;

    @InjectMocks
    private SprintService sprintService;

    /**
     * Tests that the addMilestone method calls repository.save() to save the given deadline to the database, then
     * returns the saved milestone.
     */
    @Test
    void addMilestone() {
        when(milestoneRepository.save(any(Milestone.class))).then(returnsFirstArg());
        Milestone expectedMilestone = new Milestone(0, "Test Milestone", new Date());
        Milestone milestone = milestoneService.addMilestone(expectedMilestone);

        verify(milestoneRepository, times(1)).save(expectedMilestone);
        assertEquals(expectedMilestone, milestone);
    }

    /**
     * Tests that the testRemove method calls the repository's deleteById method with the expected milestone id when a
     * milestone with the given id exists.
     */
    @Test
    void testRemoveMilestoneExists() {
        Milestone expectedMilestone = new Milestone(0, "Test Milestone", new Date());
        when(milestoneRepository.findById(any(Integer.class))).thenReturn(Optional.of(expectedMilestone));
        milestoneService.removeMilestone(expectedMilestone.getId());

        verify(milestoneRepository, times(1)).deleteById(expectedMilestone.getId());
    }

    /**
     * Tests that the testRemove method does not call the repository's deleteById method with when a milestone with the
     * given id does not exist.
     */
    @Test
    void testRemoveMilestoneDoesNotExist() {
        when(milestoneRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        milestoneService.removeMilestone(1);

        verify(milestoneRepository, times(0)).deleteById(any(Integer.class));
    }

    @Test
    void testGetMilestoneWithColour_givenOneMilestoneOccurInASprint_returnColour() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2021-1-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2021-12-22", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Milestone milestone = new Milestone();
        milestone.setMilestoneDate(sprintService.calendarDateStringToDate("2021-12-21", false));
        List<Milestone> milestoneList = new ArrayList<>();
        milestoneList.add(milestone);

        when(milestoneService.getAllEventsOrderedWithColour(sprintList)).thenReturn(milestoneList);

        List<Milestone> outputMilestoneList = milestoneService.getAllEventsOrderedWithColour(sprintList);

        assertThat(outputMilestoneList.size()).isSameAs(milestoneList.size());
        assertThat(outputMilestoneList.get(0).getColour()).isSameAs(sprint.getColour());
    }


    @Test
    void testGetMilestoneWithColour_givenOneMilestoneNotOccurInASprint_returnNull() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2021-12-22", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Milestone milestone = new Milestone();
        milestone.setMilestoneDate(sprintService.calendarDateStringToDate("2021-12-22", false));
        List<Milestone> milestoneList = new ArrayList<>();
        milestoneList.add(milestone);

        when(milestoneService.getAllEventsOrderedWithColour(sprintList)).thenReturn(milestoneList);

        List<Milestone> outputMilestoneList = milestoneService.getAllEventsOrderedWithColour(sprintList);

        assertThat(outputMilestoneList.size()).isSameAs(milestoneList.size());
        assertThat(outputMilestoneList.get(0).getColour()).isNull();
    }

    @Test
    void testMilestonesWithColour_givenMultipleMilestonesOccurInASprint_returnSprintColour() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2021-1-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2021-12-22", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Milestone milestone1 = new Milestone();
        Milestone milestone2 = new Milestone();
        Milestone milestone3 = new Milestone();
        Milestone milestone4 = new Milestone();

        milestone1.setMilestoneDate(sprintService.calendarDateStringToDate("2021-12-21", false));
        milestone2.setMilestoneDate(sprintService.calendarDateStringToDate("2021-6-27", false));
        milestone3.setMilestoneDate(sprintService.calendarDateStringToDate("2021-4-25", false));
        milestone4.setMilestoneDate(sprintService.calendarDateStringToDate("2021-2-3", false));

        List<Milestone> milestoneList = new ArrayList<>();
        milestoneList.add(milestone1);
        milestoneList.add(milestone2);
        milestoneList.add(milestone3);
        milestoneList.add(milestone4);

        when(milestoneService.getAllEventsOrderedWithColour(sprintList)).thenReturn(milestoneList);

        List<Milestone> outputMilestoneList = milestoneService.getAllEventsOrderedWithColour(sprintList);

        assertThat(outputMilestoneList.size()).isSameAs(milestoneList.size());
        assertThat(outputMilestoneList.get(0).getColour()).isSameAs(sprint.getColour());
        assertThat(outputMilestoneList.get(1).getColour()).isSameAs(sprint.getColour());
        assertThat(outputMilestoneList.get(2).getColour()).isSameAs(sprint.getColour());
        assertThat(outputMilestoneList.get(3).getColour()).isSameAs(sprint.getColour());
    }

    @Test
    void testMilestonesWithColour_givenMultipleMilestonesOccurInASprint_returnSprintColour_andNullIfOutOfRange() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2021-1-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2021-12-22", true));
        sprint.setColour("#5897fc");
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        Milestone milestone1 = new Milestone();
        Milestone milestone2 = new Milestone();
        Milestone milestone3 = new Milestone();
        Milestone milestone4 = new Milestone();

        milestone1.setMilestoneDate(sprintService.calendarDateStringToDate("2021-12-25", false));
        milestone2.setMilestoneDate(sprintService.calendarDateStringToDate("2021-6-27", false));
        milestone3.setMilestoneDate(sprintService.calendarDateStringToDate("2022-4-25", false));
        milestone4.setMilestoneDate(sprintService.calendarDateStringToDate("2021-2-3", false));

        List<Milestone> milestoneList = new ArrayList<>();
        milestoneList.add(milestone1);
        milestoneList.add(milestone2);
        milestoneList.add(milestone3);
        milestoneList.add(milestone4);

        when(milestoneService.getAllEventsOrderedWithColour(sprintList)).thenReturn(milestoneList);

        List<Milestone> outputMilestoneList = milestoneService.getAllEventsOrderedWithColour(sprintList);

        assertThat(outputMilestoneList.size()).isSameAs(milestoneList.size());
        assertThat(outputMilestoneList.get(0).getColour()).isNull();
        assertThat(outputMilestoneList.get(1).getColour()).isSameAs(sprint.getColour());
        assertThat(outputMilestoneList.get(2).getColour()).isNull();
        assertThat(outputMilestoneList.get(3).getColour()).isSameAs(sprint.getColour());
    }
}