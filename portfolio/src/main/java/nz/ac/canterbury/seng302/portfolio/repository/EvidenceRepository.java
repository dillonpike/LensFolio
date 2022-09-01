package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface EvidenceRepository  extends CrudRepository<Evidence, Integer> {
    Optional<Evidence> findById(int id);
    Set<Evidence> findByUserId(Long userId);
}