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
}