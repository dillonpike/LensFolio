package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupSettingsRepository;
import org.hibernate.ObjectNotFoundException;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Methods for getting, saving, and deleting group settings database entries.
 */
@Configuration
@Service
public class GroupSettingsService {

    @Autowired
    private GroupSettingsRepository repository;

    /**
     * Returns the group settings object in the database with the group id.
     * @param groupId group id of the group settings object
     * @return group settings object object in the database with the group id
     * @throws ObjectNotFoundException thrown if no group settings object was found
     */
    public GroupSettings getGroupSettingsByGroupId(int groupId) throws ObjectNotFoundException {
        Optional<GroupSettings> groupSettings = repository.findByGroupId(groupId);
        if (groupSettings.isPresent()) {
            return groupSettings.get();
        } else {
            throw new ObjectNotFoundException(groupId, "Unknown GroupSettings with group id");
        }
    }
}
