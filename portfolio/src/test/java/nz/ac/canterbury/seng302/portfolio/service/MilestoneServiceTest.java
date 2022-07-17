package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
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
}