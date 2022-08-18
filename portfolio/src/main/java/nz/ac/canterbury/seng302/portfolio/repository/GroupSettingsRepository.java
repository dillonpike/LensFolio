package nz.ac.canterbury.seng302.portfolio.repository;

import java.util.List;
import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import org.springframework.data.repository.CrudRepository;

public interface GroupSettingsRepository extends CrudRepository<GroupSettings, Integer> {
    GroupSettings findById(int id);

    List<GroupSettings> findAllByOrderByGroupSettingsId();

}
