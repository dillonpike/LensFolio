package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GroupService class.
 */
@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    /**
     * The class that we want to test in this case GroupService Class
     */
    @Autowired
    private static GroupService groupService = new GroupService();

    /**
     * The mocked stub so that we can mock the grpc responses
     */
    @Autowired
    private GroupsServiceGrpc.GroupsServiceBlockingStub groupsServiceBlockingStub = mock(GroupsServiceGrpc.GroupsServiceBlockingStub.class);

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        groupService.groupsServiceBlockingStub = groupsServiceBlockingStub;
    }


    /**
     * Test to check create group method works in GroupService Class
     * Expect that groupsServiceBlockingStub.createGroup() method to be called with the right content of the request
     */
    @Test
    void callCreateGroupMethod_expectCreateGroupMethodTobeCalled() {
        CreateGroupResponse response = CreateGroupResponse.newBuilder()
                .setIsSuccess(true)
                .build();
        Mockito.when(groupsServiceBlockingStub.createGroup(any(CreateGroupRequest.class))).thenReturn(response);
        ArgumentCaptor<CreateGroupRequest> captor = ArgumentCaptor.forClass(CreateGroupRequest.class);
        groupService.createNewGroup("test short", "test long");
        Mockito.verify(groupsServiceBlockingStub).createGroup(captor.capture());
        assertEquals("test long", captor.getValue().getLongName());
        assertEquals("test short", captor.getValue().getShortName());
    }

    /**
     * Test to check method to add user(s) to a groups works in GroupService Class
     * Expect that groupsServiceBlockingStub.addGroupMembers() method to be called with the right content of the request
     */
    @Test
    void callAddMemberToGroup_expectAddGroupMembersTobeCalled() {
        AddGroupMembersResponse response = AddGroupMembersResponse.newBuilder()
                        .setIsSuccess(true).build();
        Mockito.when(groupsServiceBlockingStub.addGroupMembers(any(AddGroupMembersRequest.class))).thenReturn(response);
        ArgumentCaptor<AddGroupMembersRequest> captor = ArgumentCaptor.forClass(AddGroupMembersRequest.class);
        Integer groupId = 1;
        ArrayList<Integer> userIds = new ArrayList<Integer>();
        userIds.add(1);
        groupService.addMemberToGroup(groupId, userIds);
        Mockito.verify(groupsServiceBlockingStub).addGroupMembers(captor.capture());
        assertEquals(groupId, captor.getValue().getGroupId());
        assertEquals(userIds, captor.getValue().getUserIdsList());
    }

    /**
     * Test to check method to remove user(s) from a groups works in GroupService Class
     * Expect that groupsServiceBlockingStub.removeGroupMembers() method to be called with the right content of the request
     */
    @Test
    void callRemoveMembersFromGroup_expectRemoveMembersFromGroupTobeCalled() {
        RemoveGroupMembersResponse response = RemoveGroupMembersResponse.newBuilder()
                        .setIsSuccess(true).build();
        Mockito.when(groupsServiceBlockingStub.removeGroupMembers(any(RemoveGroupMembersRequest.class))).thenReturn(response);
        ArgumentCaptor<RemoveGroupMembersRequest> captor = ArgumentCaptor.forClass(RemoveGroupMembersRequest.class);
        Integer groupId = 1;
        ArrayList<Integer> userIds = new ArrayList<Integer>();
        userIds.add(1);
        userIds.add(2);
        groupService.removeMembersFromGroup(groupId, userIds);
        Mockito.verify(groupsServiceBlockingStub).removeGroupMembers(captor.capture());
        assertEquals(groupId, captor.getValue().getGroupId());
        assertEquals(userIds, captor.getValue().getUserIdsList());
    }

    /**
     * Test to check method to modify group's details works in GroupService Class
     * Expect that groupsServiceBlockingStub.modifyGroupDetails() method to be called with the right content of the request
     */
    @Test
    void callEditGroupDetailsMethod_expectModifyGroupDetailsTobeCalled() {
        ModifyGroupDetailsResponse response = ModifyGroupDetailsResponse.newBuilder()
                        .setIsSuccess(true).build();
        Mockito.when(groupsServiceBlockingStub.modifyGroupDetails(any(ModifyGroupDetailsRequest.class))).thenReturn(response);
        ArgumentCaptor<ModifyGroupDetailsRequest> captor = ArgumentCaptor.forClass(ModifyGroupDetailsRequest.class);
        Integer groupId = 1;
        String shortName = "test short";
        String longName = "test long";
        groupService.editGroupDetails(groupId, shortName, longName);
        Mockito.verify(groupsServiceBlockingStub).modifyGroupDetails(captor.capture());
        assertEquals(groupId, captor.getValue().getGroupId());
        assertEquals(shortName, captor.getValue().getShortName());
        assertEquals(longName, captor.getValue().getLongName());
    }

    /**
     * Test to check method to delete a group works in GroupService Class
     * Expect that groupsServiceBlockingStub.deleteGroup() method to be called with the right content of the request
     */
    @Test
    void callDeleteGroupMethod_expectDeleteGroupTobeCalled() {
        DeleteGroupResponse response = DeleteGroupResponse.newBuilder()
                        .setIsSuccess(true).build();
        Mockito.when(groupsServiceBlockingStub.deleteGroup(any(DeleteGroupRequest.class))).thenReturn(response);
        ArgumentCaptor<DeleteGroupRequest> captor = ArgumentCaptor.forClass(DeleteGroupRequest.class);
        Integer groupId = 1;

        groupService.deleteGroup(groupId);
        Mockito.verify(groupsServiceBlockingStub).deleteGroup(captor.capture());
        assertEquals(groupId, captor.getValue().getGroupId());
    }

    /**
     * Test to check method to get a group details works in GroupService Class
     * Expect that groupsServiceBlockingStub.getGroupDetails() method to be called with the right content of the request
     */
    @Test
    void callGetGroupDetailsMethod_expectGetGroupDetailsTobeCalled() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder()
                        .setGroupId(1)
                        .build();

        Mockito.when(groupsServiceBlockingStub.getGroupDetails(any(GetGroupDetailsRequest.class))).thenReturn(response);
        ArgumentCaptor<GetGroupDetailsRequest> captor = ArgumentCaptor.forClass(GetGroupDetailsRequest.class);
        Integer groupId = 1;
        groupService.getGroupDetails(groupId);
        Mockito.verify(groupsServiceBlockingStub).getGroupDetails(captor.capture());
        assertEquals(groupId, captor.getValue().getGroupId());
    }

    /**
     * Test to check method to get list of groups works in GroupService Class
     * Expect that groupsServiceBlockingStub.getPaginatedGroups() method to be called with the right content of the request
     */
    @Test
    void callGetPaginatedGroupsMethod_expectGetPaginatedGroupsTobeCalled() {
        PaginatedGroupsResponse response = PaginatedGroupsResponse.newBuilder()
                .build();

        Mockito.when(groupsServiceBlockingStub.getPaginatedGroups(any(GetPaginatedGroupsRequest.class))).thenReturn(response);
        ArgumentCaptor<GetPaginatedGroupsRequest> captor = ArgumentCaptor.forClass(GetPaginatedGroupsRequest.class);
        Integer offset = 1;
        String orderBy = "test";
        boolean isAscending = true;
        groupService.getPaginatedGroups(offset, 10, orderBy, isAscending);
        Mockito.verify(groupsServiceBlockingStub).getPaginatedGroups(captor.capture());
        assertEquals(offset, captor.getValue().getOffset());
        assertEquals(isAscending, captor.getValue().getIsAscendingOrder());
        assertEquals(orderBy, captor.getValue().getOrderBy());
    }
}
