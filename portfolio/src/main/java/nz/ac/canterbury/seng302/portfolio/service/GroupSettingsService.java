package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupSettingsRepository;
import org.hibernate.ObjectNotFoundException;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;

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

    /**
     * Check if a repository has been set up or not.
     * @param groupId the id of the group in interest
     * @return True if repoId is not 0 and repoApiKey is not null, Otherwise False.
     */
    public boolean doesGroupHaveRepo(int groupId) {
        GroupSettings groupSettings = getGroupSettingsByGroupId(groupId);
        return groupSettings.getRepoId() != 0 && groupSettings.getRepoApiKey() != null;
    }

    /**
     * Method to add group setting modal attribute to the model,
     * it will set repo id to 0 if current group repository has not been set up.
     * @param model model to add group setting modal attribute to
     */
    public void addSettingAttributesToModel(Model model, GroupSettings groupSettings) {
        // Check if group setting is default
        if (groupSettings.getRepoId() != 0) {
            model.addAttribute("repoId", groupSettings.getRepoId());
        } else {
            model.addAttribute("repoId", 0);

        }
        model.addAttribute("repoName", groupSettings.getRepoName());
        model.addAttribute("repoApiKey", groupSettings.getRepoApiKey());
        model.addAttribute("groupSettingsId", groupSettings.getGroupSettingsId());
        model.addAttribute("repoServerUrl", groupSettings.getRepoUrl());
    }

    /**
     * Method to check if current group setting has been saved to the database successfully.
     * @param groupSettingId current group setting id
     * @param repoId current group setting repo id
     * @param repoName current group setting repo name
     * @param repoToken current group setting repo token
     * @param groupId current group id
     * @return true if current group setting has been saved successfully, otherwise false.
     */
    public boolean isGroupSettingSaved(int groupSettingId, long repoId, String repoName, String repoToken, int groupId, String repoServerUrl) {
        try {
            GroupSettings targetGroupSetting = new GroupSettings(repoId, repoName, repoToken, groupId, repoServerUrl);
            targetGroupSetting.setGroupSettingsId(groupSettingId);
            saveGroupSettings(targetGroupSetting);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Checks that the group settings attributes are valid for storing in the database.
     * Returns true if they are valid, otherwise false.
     * @param repoId repo id to check, must be small enough to store in an int
     * @param repoName repo name to check, must be between 1 and 30 characters (inclusive)
     * @param repoToken repo token to check, must not be between 1 and 50 characters (inclusive)
     * @return true if settings are valid, otherwise false
     */
    public boolean isValidGroupSettings(int repoId, String repoName, String repoToken) {
        int maxRepoId = 2147483647;
        int maxRepoNameLength = 30;
        int maxRepoTokenLength = 50;
        return repoId < maxRepoId &&
                0 < repoName.length() && repoName.length() <= maxRepoNameLength &&
                0 < repoToken.length() && repoToken.length() <= maxRepoTokenLength;
    }
}
