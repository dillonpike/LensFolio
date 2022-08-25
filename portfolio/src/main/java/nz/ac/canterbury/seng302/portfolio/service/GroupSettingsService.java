package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupSettingsRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.hibernate.ObjectNotFoundException;
import org.jvnet.hk2.annotations.Service;
import org.mariadb.jdbc.internal.logging.Logger;
import org.mariadb.jdbc.internal.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;

import java.util.Optional;

@Configuration
@Service
public class GroupSettingsService {

    @Autowired
    private GroupSettingsRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(GroupSettingsService.class);

    public GroupSettings getGroupSettingsByGroupId(int groupId) throws ObjectNotFoundException {
        Optional<GroupSettings> groupSettings = repository.findByGroupId(groupId);
        if (groupSettings.isPresent()) {
            return groupSettings.get();
        } else {
            // Generate a new group setting model
            GroupSettings newGroupSetting = new GroupSettings();
            newGroupSetting.setGroupId(groupId);
            repository.save(newGroupSetting);
            return newGroupSetting;
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
