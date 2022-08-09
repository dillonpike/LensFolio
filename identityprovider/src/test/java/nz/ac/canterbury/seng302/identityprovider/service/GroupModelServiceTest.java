package nz.ac.canterbury.seng302.identityprovider.service;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.repository.RolesRepository;
import nz.ac.canterbury.seng302.identityprovider.repository.UserModelRepository;
import nz.ac.canterbury.seng302.identityprovider.server.GroupModelServerService;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.directory.InvalidAttributesException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupModelServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupModelService groupModelService;

    @Mock
    private UserModelService userModelService;

    @Mock
    private UserModelRepository userRepository;

    @Mock
    private RolesRepository rolesRepository;

    private final GroupModel testGroup = new GroupModel("Test", "Test Group", 1);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * Test that getGroupById method return the correct groupModel by given an existing group Model
     */
    @Test
    void testGetExistingGroupById() {
        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(testGroup));

        try {
            GroupModel expectedGroupModel = groupModelService.getGroupById(testGroup.getGroupId());
            assertEquals(expectedGroupModel, testGroup);
        } catch (InvalidAttributesException e) {
            e.printStackTrace();
        }
    }

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

    /**
     * Checks that a user is added to the group correctly. Can't check if the group is added back to the repository correctly.
     */
    @Test
    void checkUserAddedToGroup() {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(testGroup));

        boolean wasAdded = groupModelService.addUsersToGroup(new ArrayIterator<>(new UserModel[]{testUser}), testGroup.getGroupId());

        assertTrue(wasAdded);
        assertTrue(testGroup.getMemberIds().contains(testUser.getUserId()));
    }

    @Test
    void checkUserAddedToGroupWhenUserIsAlreadyPartOf() {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(testGroup));

        boolean wasAdded = groupModelService.addUsersToGroup(new ArrayIterator<>(new UserModel[]{testUser}), testGroup.getGroupId());

        assertTrue(wasAdded);
        assertTrue(testGroup.getMemberIds().contains(testUser.getUserId()));

        wasAdded = groupModelService.addUsersToGroup(new ArrayIterator<>(new UserModel[]{testUser}), testGroup.getGroupId());

        assertTrue(wasAdded);
        assertTrue(testGroup.getMemberIds().contains(testUser.getUserId()));
    }

    @Test
    void checkUserAddedToGroupThatDoesNotExist() {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        // Group won't be returned by the repository, therefor the group doesn't exist
        boolean wasAdded = groupModelService.addUsersToGroup(new ArrayIterator<>(new UserModel[]{testUser}), testGroup.getGroupId());

        assertFalse(wasAdded);
        assertFalse(testGroup.getMemberIds().contains(testUser.getUserId()));
    }

    @Test
    void checkUserAddedToTeachersGroup() {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        GroupModel teacherTestGroup = new GroupModel("Teachers", "Teachers Group", 1);
        teacherTestGroup.setGroupId(GroupModelServerService.TEACHERS_GROUP_ID);

        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(teacherTestGroup));

        boolean wasAdded = groupModelService.addUsersToGroup(new ArrayIterator<>(new UserModel[]{testUser}), GroupModelServerService.TEACHERS_GROUP_ID);

        assertTrue(wasAdded);
        assertTrue(teacherTestGroup.getMemberIds().contains(testUser.getUserId()));
    }

    /**
     * Try to remove a user from a group after they are added.
     */
    @Test
    void removeUserFromGroup() throws InvalidAttributesException {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(testGroup));

        boolean wasAdded = groupModelService.addUsersToGroup(new ArrayIterator<>(new UserModel[]{testUser}), testGroup.getGroupId());

        assertTrue(wasAdded);
        assertTrue(testGroup.getMemberIds().contains(testUser.getUserId()));

        boolean wasRemoved = groupModelService.removeUsersFromGroup(new ArrayIterator<>(new UserModel[]{testUser}), testGroup.getGroupId());

        assertTrue(wasRemoved);
        assertFalse(testGroup.getMemberIds().contains(testUser.getUserId()));
    }

    @Test
    void removeUserFromGroupWhenNotInGroup() throws InvalidAttributesException {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(testGroup));

        assertFalse(testGroup.getMemberIds().contains(testUser.getUserId()));

        boolean wasRemoved = groupModelService.removeUsersFromGroup(new ArrayIterator<>(new UserModel[]{testUser}), testGroup.getGroupId());

        assertTrue(wasRemoved);
        assertFalse(testGroup.getMemberIds().contains(testUser.getUserId()));
    }

    @Test
    void removeUserFromGroupThatDoesNotExist() throws InvalidAttributesException {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        // Group won't be returned by the repository, therefor the group doesn't exist
        boolean wasRemoved = groupModelService.removeUsersFromGroup(new ArrayIterator<>(new UserModel[]{testUser}), testGroup.getGroupId());

        assertFalse(wasRemoved);
    }

    /**
     * Test if user is part of group, the method returns so.
     */
    @Test
    void checkIsUserPartOfGroup() {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        GroupModel testGroupCopy = new GroupModel(testGroup.getShortName(), testGroup.getLongName(), testGroup.getCourseId());
        testGroupCopy.addMember(testUser);

        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(testGroupCopy));

        boolean userIsPartOfGroup = false;
        try {
            userIsPartOfGroup = groupModelService.isUserPartOfGroup(testUser.getUserId(), testGroupCopy.getGroupId());
        } catch (InvalidAttributesException e) {
            fail();
        }

        assertTrue(userIsPartOfGroup);
    }

    /**
     * Test when user is not part of group and method is called.
     */
    @Test
    void checkIsUserNotPartOfGroup() {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        GroupModel testGroupCopy = new GroupModel(testGroup.getShortName(), testGroup.getLongName(), testGroup.getCourseId());

        when(groupRepository.findById(any(Integer.class))).thenReturn(Optional.of(testGroupCopy));

        boolean userIsPartOfGroup = false;
        try {
            userIsPartOfGroup = groupModelService.isUserPartOfGroup(testUser.getUserId(), testGroupCopy.getGroupId());
        } catch (InvalidAttributesException e) {
            fail();
        }

        assertFalse(userIsPartOfGroup);
    }

    @Test
    void checkIsUserPartOfGroupWhenGroupDoesNotExist() {
        UserModel testUser = new UserModel();
        testUser.setUserId(1);

        GroupModel testGroupCopy = new GroupModel(testGroup.getShortName(), testGroup.getLongName(), testGroup.getCourseId());

        boolean exceptionRun = false;
        try {
            // Group won't be returned by the repository, therefor the group doesn't exist
            groupModelService.isUserPartOfGroup(testUser.getUserId(), testGroupCopy.getGroupId());
        } catch (InvalidAttributesException e) {
            exceptionRun = true;
        }

        assertTrue(exceptionRun);
    }
}