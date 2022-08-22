package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupSettingsRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.hibernate.ObjectNotFoundException;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;

import java.util.Optional;

@Configuration
@Service
public class GroupSettingsService {

    @Autowired
    private GroupSettingsRepository repository;

    public GroupSettings getGroupSettingsByGroupId(int groupId) throws ObjectNotFoundException {
        Optional<GroupSettings> groupSettings = repository.findByGroupId(groupId);
        if (groupSettings.isPresent()) {
            return groupSettings.get();
        } else {
            throw new ObjectNotFoundException(groupId, "Unknown GroupSettings with group id");
        }
    }

}
