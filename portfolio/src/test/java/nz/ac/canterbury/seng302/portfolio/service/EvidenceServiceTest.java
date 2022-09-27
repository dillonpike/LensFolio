package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.HighFivers;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    private EvidenceRepository evidenceRepository;

    @Mock
    private RegisterClientService registerClientService;

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
        when(evidenceRepository.findAllByUserId(any(Integer.class))).thenReturn(testEvidences);

        List<Evidence> actualEvidences = evidenceService.getEvidences(1);
        boolean isCorrectlySorted = true;
        for(int i = 1; i < actualEvidences.size(); i++ ) {
            if(actualEvidences.get(i-1).getDate().before(actualEvidences.get(i).getDate())){
                isCorrectlySorted = false;
                break;
            }
        }
        assertTrue(isCorrectlySorted);
        verify(evidenceRepository, times(1)).findAllByUserId(1);
    }

    /**
     * Test that the evidence service can successfully save a new evidence piece to the database.
     */
    @Test
    void testAddEvidence() {
        when(evidenceRepository.save(any(Evidence.class))).thenReturn(testEvidences.get(0));
        assertTrue(evidenceService.addEvidence(testEvidences.get(0)));
    }

    /**
     * Test that the evidence service returns false when an evidence piece is not saved correctly to the database.
     */
    @Test
    void testFailAddEvidence() {
        when(evidenceRepository.save(any(Evidence.class))).thenThrow(new MockitoException("Mockito exception"));
        assertFalse(evidenceService.addEvidence(testEvidences.get(0)));
    }

    /**
     * Tests that the correct user responses are given when fetching the users who have high fived a piece of evidence.
     */
    @Test
    void testGetHighFiversOfEvidence() {
        List<HighFivers> expectedUsers = new ArrayList<>();
        Evidence testEvidence = new Evidence();
        int numUsers = 3;
        for (int i = 0; i < numUsers; i++) {
            String firstName = "First name" + i;
            String lastName = "Last name" + i;
            UserResponse userResponse = UserResponse.newBuilder().setId(i).setFirstName(firstName).setLastName(lastName).build();
            expectedUsers.add(new HighFivers(firstName + " " + lastName, i));
            testEvidence.addHighFiverId(i);
        }
        List<HighFivers> actualUsers = testEvidence.getHighFivers().stream().toList();
        for(int i=0; i < actualUsers.size(); i++){
            assertEquals(expectedUsers.get(i).getUserId(), actualUsers.get(i).getUserId());
            assertEquals(expectedUsers.get(i).getName(), actualUsers.get(i).getName());
        }
    }

    /**
     * Tests that no user responses are returned when no users have high fived a piece of evidence.
     */
    @Test
    void testGetHighFiversOfEvidenceWhenNoHighFivers() {
        Evidence testEvidence = new Evidence();
        Set<HighFivers> actualUsers = testEvidence.getHighFivers();
        assertEquals(0, actualUsers.size());
    }

}
