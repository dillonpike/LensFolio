package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupModelServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupModelService groupModelService;

    private final GroupModel testGroup = new GroupModel("Test", "Test Group", 1);

    /**
     * Tests that the repository deleteById method is called once with the correct group id when calling the
     * removeGroup function.
     */
    @Test
    void testDeleteExistingGroup() {
        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(testGroup));
        doNothing().when(groupRepository).deleteById(testGroup.getGroupId());

        boolean isSuccess = groupModelService.removeGroup(testGroup.getGroupId());

        assertTrue(isSuccess);
        Mockito.verify(groupRepository, Mockito.times(1)).deleteById(testGroup.getGroupId());
    }

    /**
     * Tests that the repository deleteById method is not called when an invalid group id is used
     * (group is not present in the repository).
     */
    @Test
    void testDeleteNonExistingGroup() {
        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        boolean isSuccess = groupModelService.removeGroup(testGroup.getGroupId());

        assertFalse(isSuccess);
        Mockito.verify(groupRepository, Mockito.times(0)).deleteById(testGroup.getGroupId());
    }

    @Test
    void testCreatingNewGroup() {
        when(groupRepository.save(any(GroupModel.class))).thenReturn(testGroup);
        GroupModel returnedGroup = groupModelService.addGroup(testGroup.getShortName(), testGroup.getLongName(), testGroup.getCourseId());
        assertEquals(testGroup, returnedGroup);
    }

    @Test
    void testCreatingInvalidNewGroup() {
        try {
            groupModelService.addGroup("", "", null);
            fail();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void testCheckingValidShortNameIsValid() {
        boolean result = groupModelService.checkShortNameIsUnique("very unique");
        assertTrue(result);
    }

    @Test
    void testCheckingInvalidShortNameIsValid() {
        GroupModel testGroup2 = new GroupModel("teacher", "Test Group 2", 1);
        when(groupRepository.findByShortName("teacher")).thenReturn(Optional.of(testGroup2));

        boolean result = groupModelService.checkShortNameIsUnique("teacher");
        assertFalse(result);
    }

    @Test
    void testCheckingValidLongNameIsValid() {
        boolean result = groupModelService.checkLongNameIsUnique("very unique");
        assertTrue(result);
    }

    @Test
    void testCheckingInvalidLongNameIsValid() {
        GroupModel testGroup1 = new GroupModel("unique", "Test Group 1", 1);
        when(groupRepository.findByLongName("Test Group 1")).thenReturn(Optional.of(testGroup1));

        boolean result = groupModelService.checkLongNameIsUnique("Test Group 1");
        assertFalse(result);
    }

    /**
     * Tests that the repository save() method is not called when an invalid group id is used
     * (group is not present in the repository).
     */
    @Test
    void testEditExistingGroup() {
        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(testGroup));

        boolean isSuccess = groupModelService.editGroup(testGroup.getGroupId(), testGroup.getShortName(), testGroup.getLongName());

        assertTrue(isSuccess);
        Mockito.verify(groupRepository, Mockito.times(1)).save(testGroup);
    }

    /**
     * Tests that the repository save() method is not called when an invalid group id is used
     * (group is not present in the repository).
     */
    @Test
    void testEditNonExistingGroup() {
        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        boolean isSuccess = groupModelService.editGroup(testGroup.getGroupId(), testGroup.getShortName(), testGroup.getLongName());

        assertFalse(isSuccess);
        Mockito.verify(groupRepository, Mockito.times(0)).save(testGroup);
    }

    /**
     * Tests that the checkShortNameIsUniqueEditing method returns true when the given short name is not shared by any
     * other group.
     */
    @Test
    void testCheckShortNameIsUniqueEditingIsUnique() {
        when(groupRepository.findByShortName(testGroup.getShortName())).thenReturn(Optional.of(testGroup));

        boolean isUnique = groupModelService.checkShortNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getShortName());

        assertTrue(isUnique);
    }

    /**
     * Tests that the checkLongNameIsUniqueEditing method returns true when the given long name is not shared by any
     * other group.
     */
    @Test
    void testCheckLongNameIsUniqueEditingIsUnique() {
        when(groupRepository.findByLongName(testGroup.getLongName())).thenReturn(Optional.of(testGroup));

        boolean isUnique = groupModelService.checkLongNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getLongName());

        assertTrue(isUnique);
    }

    /**
     * Tests that the checkShortNameIsUniqueEditing method returns false when the given short name is shared by another
     * group.
     */
    @Test
    void testCheckShortNameIsUniqueEditingIsNotUnique() {
        when(groupRepository.findByShortName(testGroup.getShortName())).thenReturn(Optional.of(testGroup));

        boolean isUnique = groupModelService.checkShortNameIsUniqueEditing(2, testGroup.getShortName());

        assertFalse(isUnique);
    }

    /**
     * Tests that the checkLongNameIsUniqueEditing method returns false when the given short name is shared by another
     * group.
     */
    @Test
    void testCheckLongNameIsUniqueEditingIsNotUnique() {
        when(groupRepository.findByLongName(testGroup.getLongName())).thenReturn(Optional.of(testGroup));

        boolean isUnique = groupModelService.checkLongNameIsUniqueEditing(2, testGroup.getLongName());

        assertFalse(isUnique);
    }
}