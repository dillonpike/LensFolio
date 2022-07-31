package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


/**
 * Contains methods for performing operations on Group objects, such as adding and removing group members, and storing
 * groups in the database.
 */
@Service
public class GroupService {

    /**
     * Repository of Group objects.
     */
    @GrpcClient(value = "identity-provider-grpc-server")
    GroupsServiceGrpc.GroupsServiceBlockingStub groupsServiceBlockingStub;

    public CreateGroupResponse createNewGroup(String shortName, String longName){
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setShortName(shortName)
                .setLongName(longName)
                .build();
        return groupsServiceBlockingStub.createGroup(request);
    }

    public AddGroupMembersResponse addMemberToGroup(Integer groupId, ArrayList<Integer> userIds){
        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder()
                .setGroupId(groupId)
                .addAllUserIds(userIds)
                .build();
        return groupsServiceBlockingStub.addGroupMembers(request);
    }

    public RemoveGroupMembersResponse removeMembersFromGroup(Integer groupId, ArrayList<Integer> userIds){
        RemoveGroupMembersRequest request = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(groupId)
                .addAllUserIds(userIds)
                .build();
        return groupsServiceBlockingStub.removeGroupMembers(request);
    }

    public ModifyGroupDetailsResponse editGroupDetails(Integer groupId, String shortName, String longName){
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(groupId)
                .setShortName(shortName)
                .setLongName(longName)
                .build();
        return groupsServiceBlockingStub.modifyGroupDetails(request);
    }

    public DeleteGroupResponse deleteGroup(Integer groupId){
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder()
                .setGroupId(groupId)
                .build();
        return groupsServiceBlockingStub.deleteGroup(request);
    }

    public GroupDetailsResponse getGroupDetails(Integer groupId){
        GetGroupDetailsRequest request = GetGroupDetailsRequest.newBuilder()
                .setGroupId(groupId)
                .build();
        return groupsServiceBlockingStub.getGroupDetails(request);
    }

    public PaginatedGroupsResponse getPaginatedGroups(Integer offset, Integer limit, String orderBy, boolean isAscending){
        GetPaginatedGroupsRequest request = GetPaginatedGroupsRequest.newBuilder()
                .setOffset(offset)
                .setIsAscendingOrder(isAscending)
                .setOrderBy(orderBy)
                .build();
        return groupsServiceBlockingStub.getPaginatedGroups(request);
    }
}
