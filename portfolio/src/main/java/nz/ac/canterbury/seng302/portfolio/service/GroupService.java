package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.NotificationResponse;
import nz.ac.canterbury.seng302.portfolio.utility.Toast;
import nz.ac.canterbury.seng302.portfolio.utility.ToastUtility;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Contains methods for performing operations on Group objects, such as adding and removing group members, and storing
 * groups in the database.
 */
@Service
public class GroupService {

    /**
     * Accessing GRPC class to send request and get response about group to identity provider
     */
    @GrpcClient(value = "identity-provider-grpc-server")
    GroupsServiceGrpc.GroupsServiceBlockingStub groupsServiceBlockingStub;

    private List<GroupDetailsResponse> groupDetailsResponseList;

    private List<UserResponse> userResponseList;

    private List<NotificationResponse> groupsToDisplay = new ArrayList<>();

    /***
     * Method to create group by sending request using GRPC to the idp
     * @param shortName (String) the short name of the new group that will be created and persisted in database
     * @param longName (String) the long name of the new group that will be created and persisted in database
     * @return (CreateGroupResponse) contain the response of group creation
     */
    public CreateGroupResponse createNewGroup(String shortName, String longName){
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setShortName(shortName)
                .setLongName(longName)
                .build();
        return groupsServiceBlockingStub.createGroup(request);
    }

    /***
     * Method to add user(s) to an existing group by sending request using GRPC to the idp
     * @param groupId (Integer) id of the group
     * @param userIds (ArrayList<Integer>) a list of all the user ids that will be added to a group
     * @return (AddGroupMembersResponse) contains the response of addition of user(s) to a group
     */
    public AddGroupMembersResponse addMemberToGroup(Integer groupId, List<Integer> userIds){
        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder()
                .setGroupId(groupId)
                .addAllUserIds(userIds)
                .build();
        return groupsServiceBlockingStub.addGroupMembers(request);
    }

    /***
     * Method to remove user(s) from an existing group by sending request using GRPC to the idp
     * @param groupId (Integer) id of the group
     * @param userIds (ArrayList<Integer>) a list of all the user ids that will be removed from a group
     * @return (RemoveGroupMembersResponse) contains the response of removal of user(s) from a group
     */
    public RemoveGroupMembersResponse removeMembersFromGroup(Integer groupId, List<Integer> userIds){
        RemoveGroupMembersRequest request = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(groupId)
                .addAllUserIds(userIds)
                .build();
        return groupsServiceBlockingStub.removeGroupMembers(request);
    }

    /***
     * Method to remove user(s) from an existing group by sending request using GRPC to the idp
     * @param groupId (Integer) id of the group
     * @param shortName (String) the new short name of the group
     * @param longName (String) the new long name of the group
     * @return (ModifyGroupDetailsResponse) contains the response of removal of user(s) to a group
     */
    public ModifyGroupDetailsResponse editGroupDetails(Integer groupId, String shortName, String longName){
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(groupId)
                .setShortName(shortName)
                .setLongName(longName)
                .build();
        return groupsServiceBlockingStub.modifyGroupDetails(request);
    }

    /***
     * Method to delete an existing group from database by sending request using GRPC to the idp
     * @param groupId (Integer) id of the group
     * @return (DeleteGroupResponse) contains the response after deletion of a group
     */
    public DeleteGroupResponse deleteGroup(Integer groupId){
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder()
                .setGroupId(groupId)
                .build();
        return groupsServiceBlockingStub.deleteGroup(request);
    }

    /***
     * Method to get the detail information of a group by sending request using GRPC to the idp
     * @param groupId (Integer) id of the group
     * @return (GroupDetailsResponse) contains the details of a group requested
     */
    public GroupDetailsResponse getGroupDetails(Integer groupId){
        GetGroupDetailsRequest request = GetGroupDetailsRequest.newBuilder()
                .setGroupId(groupId)
                .build();
        return groupsServiceBlockingStub.getGroupDetails(request);
    }

    /***
     * Method to delete an existing group from database by sending request using GRPC to the idp
     * @param offset (Integer) number used to identify the starting point to return rows from a result set
     * @param limit (Integer) number to determine how many rows are returned from a query(this is ignored due to we don't implement pagination)
     * @param orderBy (string) type of sorting
     * @param isAscending (bool) descending/ascending
     * @return (PaginatedGroupsResponse) contains list of all groups requested
     */
    public PaginatedGroupsResponse getPaginatedGroups(Integer offset, Integer limit, String orderBy, boolean isAscending){
        GetPaginatedGroupsRequest request = GetPaginatedGroupsRequest.newBuilder()
                .setOffset(offset)
                .setIsAscendingOrder(isAscending)
                .setOrderBy(orderBy)
                .build();
        return groupsServiceBlockingStub.getPaginatedGroups(request);
    }

    /**
     * Method to convert paginatedGroupsResponse to a group list.
     * Send group list attribute to the model
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     */
    public void addGroupListToModel(Model model) {
        PaginatedGroupsResponse groupList = getPaginatedGroups(1, 1, "null", false);
        groupDetailsResponseList = groupList.getGroupsList();
        model.addAttribute("groupList", groupDetailsResponseList);
    }

    /**
     * Method to convert Current groupDetailsResponse,
     * send attributes(e.g. short name, long name, group members) to the model
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param groupId Current selected group ID
     */
    public void addGroupDetailToModel(Model model, Integer groupId) {
        GroupDetailsResponse groupDetailsResponse = getGroupDetails(groupId);
        userResponseList = groupDetailsResponse.getMembersList();

        model.addAttribute("groupLongName", groupDetailsResponse.getLongName());
        model.addAttribute("groupShortName", groupDetailsResponse.getShortName());

        model.addAttribute("group", groupDetailsResponse);
        model.addAttribute("members", userResponseList);
    }

    /**
     * Adds the group validation error messages to corresponding model attributes.
     * @param model model to add error messages to
     * @param errors list of error messages to add to the model
     */
    public void addGroupNameErrorsToModel(Model model, List<ValidationError> errors) {
        for (ValidationError error : errors) {
            String errorMessage = error.getErrorText();
            if (errorMessage.contains("Short")) {
                model.addAttribute("groupShortNameAlertMessage", error.getErrorText());
            }
            if (errorMessage.contains("Long")) {
                model.addAttribute("groupLongNameAlertMessage", error.getErrorText());
            }
        }
    }
    /**
     * This method makes it so that toasts are displayed when editing/adding something in the modal
     * @param model model attribute to add variables into html
     * @param numOfToasts the number of toasts that will be displayed
     */
    public void addToastsToModel(Model model, Integer numOfToasts) {
        ToastUtility.addToastsToModel(model, groupsToDisplay, numOfToasts);
    }

    /**
     * Adds the notification so that the toast can be displayed
     * @param response sends the message that something is being edited
     * @param numOfToasts the number of toasts that will be displayed
     */
    public void addNotification(NotificationResponse response, Integer numOfToasts) {
        groupsToDisplay.add(response);
        while (groupsToDisplay.size() > numOfToasts) {
            groupsToDisplay.remove(0);
        }
    }



}
