package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for evidence pieces.
 */
@Service
public class EvidenceService {

    @Autowired
    private EvidenceRepository evidenceRepository;

    /**
     * Save a new evidence piece to the database.
     * @param newEvidence New evidence piece to be saved.
     * @return Whether the evidence was successfully saved.
     */
    public boolean addEvidence(Evidence newEvidence) {
        try {
            evidenceRepository.save(newEvidence);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
