package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains methods for saving, deleting, updating and retrieving evidence objects to the database.
 */
@Service
public class EvidenceService {

    @Autowired
    private EvidenceRepository evidenceRepository;

    /**
     * This function returns all evidences based on the userId.
     * @param userId the ID of a user who we want to get evidences for.
     * @return List of evidences.
     */
    public List<Evidence> getEvidences(int userId) {
        List<Evidence> listEvidences = evidenceRepository.findAllByUserId(userId);
        return listEvidences.stream().sorted((o1, o2)->o2.getDate().
                compareTo(o1.getDate())).toList();
    }

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
