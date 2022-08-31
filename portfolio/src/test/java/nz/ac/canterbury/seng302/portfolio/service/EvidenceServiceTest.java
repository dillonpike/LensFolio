package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link EvidenceService} class.
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class EvidenceServiceTest {
    @Mock
    private EvidenceRepository repository;

    @InjectMocks
    private EvidenceService evidenceService;

    private static final List<Evidence> testEvidences = new ArrayList<>();

    /**
     * setUp list of Evidences for testing which will returned when mocking the repository's method which return list of Evidences.
     */
    @BeforeEach
    void setUp() {
        Evidence evidence1 = new Evidence(0, 1, "testEvidence1", "testEvidence1", new Date(100));
        Evidence evidence2 = new Evidence(0, 1, "testEvidence2", "testEvidence2", new Date(500));
        Evidence evidence3 = new Evidence(0, 1, "testEvidence3", "testEvidence3", new Date(300));
        Evidence evidence4 = new Evidence(0, 1, "testEvidence4", "testEvidence4", new Date(200));
        testEvidences.add(evidence1);
        testEvidences.add(evidence2);
        testEvidences.add(evidence3);
        testEvidences.add(evidence4);
    }

    /**
     * Tests that the getAllEvidences method returns all evidences based on the userId.
     * This test also check that the method return sorted evidence by date. ( evidence with the latest date should be first)
     */
    @Test
    void getEvidences() {
        when(repository.findAllByUserId(any(Integer.class))).thenReturn(testEvidences);

        List<Evidence> actualEvidences = evidenceService.getEvidences(1);
        boolean isCorrectlySorted = true;
        for(int i = 1; i < actualEvidences.size(); i++ ) {
            if(actualEvidences.get(i-1).getDate().before(actualEvidences.get(i).getDate())){
                isCorrectlySorted = false;
                break;
            }
        }
        assertTrue(isCorrectlySorted);
        verify(repository, times(1)).findAllByUserId(1);
    }
}