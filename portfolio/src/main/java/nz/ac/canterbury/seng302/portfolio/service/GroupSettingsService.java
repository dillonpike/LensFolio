package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupSettingsRepository;
import org.hibernate.ObjectNotFoundException;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(GroupSettingsService.class);

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

    /**
     * Saves the group settings object to the database. This will create a new entry in the database if there isn't an
     * entry in the database with the same group settings id.
     * @param groupSettings group settings object to save
     * @return the saved object
     */
    public GroupSettings saveGroupSettings(GroupSettings groupSettings) {
        logger.info("Saving group settings {} ({}) for group {}",
                groupSettings.getGroupSettingsId(), groupSettings.getRepoName(), groupSettings.getGroupId());
        return repository.save(groupSettings);
    }

    /**
     * Deletes the group entry in the database with the group settings id.
     * Returns true if an entry is found and deleted, otherwise false.
     * @param groupSettingsId id of the group settings entry to delete
     * @return true if an entry is found and deleted, otherwise false
     */
    public boolean deleteGroupSettings(int groupSettingsId) {
        Optional<GroupSettings> groupSettings = repository.findById(groupSettingsId);
        if (groupSettings.isPresent()) {
            logger.info("Deleting group settings {} ({}) for group {}",
                    groupSettingsId, groupSettings.get().getRepoName(), groupSettings.get().getGroupId());
            repository.deleteById(groupSettingsId);
            return true;
        }
        return false;
    }
}
