package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for GroupService class.
 */
@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    /**
     * Mocked repository of Group objects.
     */
    @Mock
    private GroupRepository groupRepository;

    /**
     * GroupService object.
     */
    @InjectMocks
    private GroupService groupService = new GroupService();

    /**
     * Group object used in tests.
     */
    private final Group expectedGroup = new Group("shortName", "fullName", 2);

    /**
     * Message in exception that should be thrown when a Group object can't be found with the given id.
     */
    private final String expectedFindExceptionMessage = "Group not found";

    /**
     * Given that there are no Group objects stored in the database, then the getGroupById method should throw an
     * Exception.
     */
    @Test
    public void givenNoStoredGroups_whenGetGroupById_thenThrowException() {
        given(groupRepository.findById(any(Integer.class))).willReturn(Optional.empty());
        try {
            groupService.getGroupById(1);
            fail();
        } catch (Exception exception) {
            assertEquals(expectedFindExceptionMessage, exception.getMessage());
        }
    }

    /**
     * Given that there are Group objects stored in the database, then the getGroupById method should return the
     * expected Group object.
     */
    @Test
    public void givenStoredGroups_whenGetGroupById_thenReturnExpectedGroup() {
        given(groupRepository.findById(any(Integer.class))).willReturn(Optional.of(expectedGroup));
        try {
            Group group = groupService.getGroupById(1);
            assertEquals(expectedGroup, group);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests that adding members to groups works as expected.
     */
    @Test
    public void givenMemberNotInGroup_whenAddMember_thenMemberInGroup() {
        int expectedUserId = 1;
        Group group = new Group("", "", 1);
        assertFalse(group.getMemberIds().contains(expectedUserId));
        groupService.addMember(expectedUserId, group);
        assertTrue(group.getMemberIds().contains(expectedUserId));
    }

    /**
     * Tests that removing members from groups works as expected.
     */
    @Test
    public void givenMemberInGroup_whenRemoveMember_thenMemberNotInGroup() {
        int expectedUserId = 1;
        Group group = new Group("", "", 1);
        groupService.addMember(expectedUserId, group);
        assertTrue(group.getMemberIds().contains(expectedUserId));
        groupService.removeMember(expectedUserId, group);
        assertFalse(group.getMemberIds().contains(expectedUserId));
    }
}
