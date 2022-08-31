package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvidenceService {

    @Autowired
    private EvidenceRepository evidenceRepository;

    public List<Evidence> getEvidences(int userId) {
        List<Evidence> listEvidences = evidenceRepository.findAllByUserId(userId);
        return listEvidences.stream().sorted((o1, o2)->o2.getDate().
                compareTo(o1.getDate())).toList();
    }
}
