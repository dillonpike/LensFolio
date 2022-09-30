package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository for evidence.
 */
public interface EvidenceRepository  extends CrudRepository<Evidence, Integer> {
    Optional<Evidence> findById(int id);
    Set<Evidence> findByUserId(Long userId);
    List<Evidence> findAllByUserId(int userId);
    List<Evidence> findAllByUserIdOrderByDateDescEvidenceIdDesc(int userId);
    Evidence findByEvidenceId(int evidenceId);
}
