package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
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
        Milestone expectedMilestone = new Milestone(0,"Test Milestone", new Date());
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
        Milestone expectedMilestone = new Milestone(0,"Test Milestone", new Date());
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
    void givenSprint_returnEventsThatOverlap() {
        Sprint sprint = new Sprint();
        sprint.setName("Testing");
        sprint.setStartDate(sprintService.calendarDateStringToDate("2001-12-20", false));
        sprint.setEndDate(sprintService.calendarDateStringToDate("2001-12-22", true));
        sprint.setColour("#5897fc");

        Milestone milestone1 = new Milestone();
        Milestone milestone2 = new Milestone();
        Milestone milestone3 = new Milestone();
        Milestone milestone4 = new Milestone();

        milestone1.setMilestoneName("Date is within");
        milestone1.setMilestoneDate(sprintService.calendarDateStringToDate("2001-12-21", false));

        milestone2.setMilestoneName("Date overlaps start");
        milestone2.setMilestoneDate(sprintService.calendarDateStringToDate("2001-12-20", false));

        milestone3.setMilestoneName("Date doesnt overlap");
        milestone3.setMilestoneDate(sprintService.calendarDateStringToDate("2001-12-24", false));

        milestone4.setMilestoneName("Date overlaps end");
        milestone4.setMilestoneDate(sprintService.calendarDateStringToDate("2001-12-22", true));

        List<Milestone> milestoneList = new ArrayList<>();
        milestoneList.add(milestone1);
        milestoneList.add(milestone2);
        milestoneList.add(milestone3);
        milestoneList.add(milestone4);

        when(milestoneService.getAllMilestonesOrdered()).thenReturn(milestoneList);

        List<Milestone> returnedEvents = milestoneService.getAllMilestonesOverlappingWithSprint(sprint);

        assertEquals(milestone1.getMilestoneName(), returnedEvents.get(0).getMilestoneName());
        assertEquals(milestone2.getMilestoneName(), returnedEvents.get(1).getMilestoneName());
        assertEquals(milestone4.getMilestoneName(), returnedEvents.get(2).getMilestoneName());

    }
}