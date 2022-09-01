package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class EvidenceServiceTest {

    @Mock
    private EvidenceRepository evidenceRepository;

    @InjectMocks
    private EvidenceService evidenceService;

    private Evidence testEvidence = new Evidence(0, 0, "Test Evidence", "This is a test evidence piece", new Date());

    @Test
    void testAddEvidence() {
        when(evidenceRepository.save(any(Evidence.class))).thenReturn(testEvidence);
        assertTrue(evidenceService.addEvidence(testEvidence));
    }
}
