package nz.ac.canterbury.seng302.portfolio.service;

import org.hibernate.ObjectNotFoundException;
import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupSettingsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link GitLabApiService} class.
 * Mock the GroupSettingsService class and the GitLabAPI.
 */
@ExtendWith(MockitoExtension.class)
class GroupSettingsServiceTest {

    @Mock
    private GroupSettingsRepository repository;

    @InjectMocks
    private GroupSettingsService groupSettingsService;

    private final GroupSettings testGroupSettings = new GroupSettings(12345, "test repo", "kjbfdsouoih321312ewln", 1);

    /**
     * Test the getGroupSettingsByGroupId method when passing in a valid group id.
     */
    @Test
    void testGetGroupSettingsByValidGroupId() {
        when(repository.findByGroupId(testGroupSettings.getGroupId())).thenReturn(Optional.of(testGroupSettings));
        assertEquals(testGroupSettings, groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId()));
    }

    /**
     * Test the getGroupSettingsByGroupId method when passing in an invalid group id.
     */
    @Test
    void testGetGroupSettingsByInvalidGroupId() {
        when(repository.findByGroupId(testGroupSettings.getGroupId())).thenReturn(Optional.empty());
        int testGroupId = testGroupSettings.getGroupId();
        assertThrows(ObjectNotFoundException.class, () -> groupSettingsService.getGroupSettingsByGroupId(testGroupId));
    }
}