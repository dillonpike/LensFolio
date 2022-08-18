package nz.ac.canterbury.seng302.portfolio.repository;

import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupSettingsRepository extends CrudRepository<GroupSettings, Integer> {
    Optional<GroupSettings> findByGroupId(int id);

    List<GroupSettings> findAllByOrderByGroupSettingsId();

}
