package nz.ac.canterbury.seng302.identityprovider.repository;

import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.model.GroupSettingsModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GroupSettingsRepository extends CrudRepository<GroupSettingsModel, Integer> {
    Optional<GroupSettingsModel> findByGroupSettingId (Integer id);


}
