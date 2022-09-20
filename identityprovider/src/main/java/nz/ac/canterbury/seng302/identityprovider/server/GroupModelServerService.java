package nz.ac.canterbury.seng302.identityprovider.server;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.service.GroupModelService;
import nz.ac.canterbury.seng302.identityprovider.service.UserModelService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.directory.InvalidAttributesException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles GRPC requests from the portfolio by performing actions related to groups, such as deleting a group, and
 * returns responses.
 */
@GrpcService
public class GroupModelServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(GroupModelServerService.class);

    @Autowired
    private GroupModelService groupModelService;

    @Autowired
    private GroupRepository repository;

    @Autowired
    private UserModelService userModelService;

    public static final Integer MEMBERS_WITHOUT_GROUP_ID = 1;

    public static final Integer TEACHERS_GROUP_ID = 2;

    private boolean firstTimeLoadUsers = true;

    private static final String NAME_WAS_NOT_UNIQUE = "Name was not unique";

    /**
     * Attempts to delete a group with the id in the request. Sends a response with an isSuccess value and message.
     * @param request request that contains a group id
     * @param responseObserver used to send the response
     */
    @Override
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        DeleteGroupResponse.Builder reply = DeleteGroupResponse.newBuilder();

        Integer groupId = null;
        try {
            groupId = request.getGroupId();
            GroupModel group = groupModelService.getGroupById(request.getGroupId());
            Set<UserModel> users = group.getMembers();

            if (groupModelService.removeGroup(groupId)) {
                for (UserModel user : users) {
                    user.getGroups().remove(group);
                    if (user.getGroups().isEmpty()) {
                        groupModelService.addUsersToGroup(new ArrayIterator<>(new UserModel[]{user}) , MEMBERS_WITHOUT_GROUP_ID);
                    }
                }
                responseObserver.onNext(reply.setIsSuccess(true).setMessage("Successful").build());
            } else {
                responseObserver.onNext(reply.setIsSuccess(false).setMessage("Unsuccessful").build());
            }
        } catch (InvalidAttributesException e) {
            logger.error(MessageFormat.format("Group {0} does not exist, so cannot be retrieved. ", groupId));
            responseObserver.onNext(reply.setIsSuccess(false).setMessage("Unsuccessful").build());
        }
        responseObserver.onCompleted();
    }

    /**
     * Gets all members without a group (in the group for users without any other groups).
     * @param responseStreamObserver Response sent to portfolio.
     */
    @Override
    public void getMembersWithoutAGroup(Empty ignore, StreamObserver<GroupDetailsResponse> responseStreamObserver) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();
        reply.setGroupId(MEMBERS_WITHOUT_GROUP_ID);
        Set<Integer> userIDs = new HashSet<>();
        GroupModel groupModel;
        try {
            userIDs = groupModelService.getMembersOfGroup(MEMBERS_WITHOUT_GROUP_ID);
            groupModel = groupModelService.getGroupById(MEMBERS_WITHOUT_GROUP_ID);
            reply.setLongName(groupModel.getLongName());
            reply.setShortName(groupModel.getShortName());
        } catch (InvalidAttributesException e) {
            responseStreamObserver.onError(e);
        }

        List<UserResponse> listOfUsers = userModelService.getUserInformationByList(userIDs);
        for (UserResponse user : listOfUsers) {
            reply.addMembers(user);
        }

        responseStreamObserver.onNext(reply.build());
        responseStreamObserver.onCompleted();
    }

    /**
     * Creates a group and returns information about whether it was done or not.
     * @param request Request from Portfolio.
     * @param responseObserver Response given back to Portfolio.
     */
    @Override
    public void createGroup(CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
        CreateGroupResponse.Builder reply = CreateGroupResponse.newBuilder();
        boolean shortNameUnique = groupModelService.checkShortNameIsUnique(request.getShortName());
        boolean longNameUnique = groupModelService.checkLongNameIsUnique(request.getLongName());
        if (!shortNameUnique) {
            ValidationError.Builder error = ValidationError.newBuilder();
            error.setErrorText("Short Name not unique");
            error.setFieldName("shortName");
            reply.addValidationErrors(error.build());
            reply.setIsSuccess(false).setMessage(NAME_WAS_NOT_UNIQUE);
        }
        if (!longNameUnique) {
            ValidationError.Builder error = ValidationError.newBuilder();
            error.setErrorText("Long Name not unique");
            error.setFieldName("longName");
            reply.addValidationErrors(error.build());
            reply.setIsSuccess(false).setMessage(NAME_WAS_NOT_UNIQUE);
        }
        if(shortNameUnique && longNameUnique) {
            try {
                GroupModel group = groupModelService.addGroup(request.getShortName(), request.getLongName(), 1);
                reply.setIsSuccess(true).setMessage("Group created").setNewGroupId(group.getGroupId());
            } catch (Exception e) {
                reply.setIsSuccess(false).setMessage("Something went wrong creating the group");
            }
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Attempts to edit a group with the id in the request. Sends a response with an isSuccess value, message, and
     * validation errors if applicable.
     * @param request request that contains a group id
     * @param responseObserver used to send the response
     */
    @Override
    public void modifyGroupDetails(ModifyGroupDetailsRequest request, StreamObserver<ModifyGroupDetailsResponse> responseObserver) {
        ModifyGroupDetailsResponse.Builder reply = ModifyGroupDetailsResponse.newBuilder();

        boolean shortNameUnique = groupModelService.checkShortNameIsUniqueEditing(request.getGroupId(), request.getShortName());
        boolean longNameUnique = groupModelService.checkLongNameIsUniqueEditing(request.getGroupId(), request.getLongName());

        addShortNameValidationError(shortNameUnique, reply);
        addLongNameValidationError(longNameUnique, reply);

        if (shortNameUnique && longNameUnique) {
            try {
                if (groupModelService.editGroup(request.getGroupId(), request.getShortName(), request.getLongName())) {
                    reply.setIsSuccess(true).setMessage("Group saved");
                } else {
                    reply.setIsSuccess(false).setMessage("Group not found");
                }
            } catch (Exception e) {
                reply.setIsSuccess(false);
                if (request.getLongName().length() > 30) {
                    reply.setMessage("Long name must be 30 characters or less");
                } else if (request.getShortName().length() > 10) {
                    reply.setMessage("Short name must be 10 characters or less");
                } else {
                    reply.setMessage("Something went wrong saving the group");
                }
            }
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
    /**
     * Method to request paginated groups from database, send PaginatedGroupsResponse response back to portfolio.
     * @param request GetPaginatedGroupsRequest request from portfolio
     * @param responseObserver Response given back to Portfolio.
     */
    @Override
    public void getPaginatedGroups(GetPaginatedGroupsRequest request, StreamObserver<PaginatedGroupsResponse> responseObserver) {
        PaginatedGroupsResponse.Builder reply = PaginatedGroupsResponse.newBuilder();
        List<GroupModel> allGroups = groupModelService.getAllGroups();
        for (GroupModel groupModel : allGroups) {
            reply.addGroups(groupModelService.getGroupInfo(groupModel));
        }
        if (firstTimeLoadUsers) {
            userModelService.usersAddedToUsersWithoutGroup(groupModelService.getMembersWithoutAGroup());
            firstTimeLoadUsers = false;
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Attempts to get group's detail with a group ID in request. Send a response with group detail.
     * @param request GetGroupDetailsRequest which contains a group ID
     * @param responseObserver used to send the response to portfolio
     */
    @Override
    public void getGroupDetails(GetGroupDetailsRequest request, StreamObserver<GroupDetailsResponse> responseObserver) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();
        if (groupModelService.isExistById(request.getGroupId())) {

            GroupModel groupModel = repository.getGroupModelByGroupId(request.getGroupId());
            reply.setGroupId(groupModel.getGroupId());
            reply.setLongName(groupModel.getLongName());
            reply.setShortName(groupModel.getShortName());

            Set<UserModel> userModelList = groupModel.getMembers();
            for (UserModel userModel : userModelList) {
                reply.addMembers(userModelService.getUserInfo(userModel));
            }
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Adds the users in the request to the group in the request.
     * @param request contains group and id and user ids
     * @param responseObserver used to send the response to portfolio
     */
    @Override
    public void addGroupMembers(AddGroupMembersRequest request, StreamObserver<AddGroupMembersResponse> responseObserver) {
        AddGroupMembersResponse.Builder reply = AddGroupMembersResponse.newBuilder();
        Iterable<UserModel> users = userModelService.getUsersByIds(request.getUserIdsList());
        boolean isSuccess;
        if (request.getGroupId() == (TEACHERS_GROUP_ID)) {
            checkUsersInTeachersGroup(users);
            groupModelService.removeFromMembersWithoutAGroup(users);
            // This is done as it assumes there's no returned issues with adding the user to the teachers group.
            // If roles are not being added or users not being added to the group correctly, check logs.
            isSuccess = true;
        } else if (request.getUserIdsList().isEmpty()) {
            isSuccess = false;
        } else {
            isSuccess = groupModelService.addUsersToGroup(users, request.getGroupId());
        }
        if (request.getGroupId() == MEMBERS_WITHOUT_GROUP_ID && isSuccess) {
            checkUsersNotInTeachersGroup(users);
            userModelService.setOnlyGroup(users, groupModelService.getMembersWithoutAGroup());
        }
        reply.setIsSuccess(isSuccess);
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Checks to see if a list of users are part of the teachers group. If not, it adds them to it.
     * Also adds the teacher role to the user, if they don't have it already.
     * @param users users to check if they are in the teachers group and have the teacher role.
     */
    public void checkUsersInTeachersGroup(Iterable<UserModel> users) {
        for (UserModel user : users) {
            boolean addedToGroup = groupModelService.addUsersToGroup(new ArrayIterator<>(new UserModel[]{user}), GroupModelServerService.TEACHERS_GROUP_ID);
            if (!addedToGroup) {
                logger.error("Something went wrong with the teachers group");
            }
            boolean roleWasAdded = userModelService.checkUserHasTeacherRole(user);
            if (!roleWasAdded) {
                logger.warn("User {} was not given teacher role. ", user.getUserId());
            }
        }
    }

    /**
     * Adds a validation error to the response if the short name is not unique.
     * @param shortNameUnique boolean value indicating if the short name is unique
     * @param reply reply to add the validation error to
     */
    private void addShortNameValidationError(boolean shortNameUnique, ModifyGroupDetailsResponse.Builder reply) {
        if (!shortNameUnique) {
            ValidationError.Builder error = ValidationError.newBuilder();
            error.setErrorText("Short Name not unique");
            error.setFieldName("shortName");
            reply.addValidationErrors(error.build());
            reply.setIsSuccess(false).setMessage(NAME_WAS_NOT_UNIQUE);
        }
    }

    /**
     * Adds a validation error to the response if the long name is not unique.
     * @param longNameUnique boolean value indicating if the long name is unique
     * @param reply reply to add the validation error to
     */
    private void addLongNameValidationError(boolean longNameUnique, ModifyGroupDetailsResponse.Builder reply) {
        if (!longNameUnique) {
            ValidationError.Builder error = ValidationError.newBuilder();
            error.setErrorText("Long Name not unique");
            error.setFieldName("longName");
            reply.addValidationErrors(error.build());
            reply.setIsSuccess(false).setMessage(NAME_WAS_NOT_UNIQUE);
        }
    }

    /**
     * Adds the users in the request to the group in the request.
     * @param request contains group and id and user ids
     * @param responseObserver used to send the response to portfolio
     */
    @Override
    public void removeGroupMembers(RemoveGroupMembersRequest request, StreamObserver<RemoveGroupMembersResponse> responseObserver) {
        RemoveGroupMembersResponse.Builder reply = RemoveGroupMembersResponse.newBuilder();
        Iterable<UserModel> users = userModelService.getUsersByIds(request.getUserIdsList());
        boolean isSuccess = false;
        if (request.getUserIdsList().isEmpty()) {
            isSuccess = false;
        } else if (request.getGroupId() == (TEACHERS_GROUP_ID)) {
            checkUsersNotInTeachersGroup(users);
            // This is done as it assumes there's no returned issues with removing the user from the teachers group.
            // If roles are not being removed or users not being removed to the group correctly, check logs.
            isSuccess = true;
        } else{
            try {
                isSuccess = groupModelService.removeUsersFromGroup(users, request.getGroupId());
            } catch (InvalidAttributesException e) {
                isSuccess = false;
            }
        }
        if (request.getGroupId() == MEMBERS_WITHOUT_GROUP_ID && isSuccess) {
            userModelService.setOnlyGroup(users, groupModelService.getMembersWithoutAGroup());
        }
        reply.setIsSuccess(isSuccess);
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Checks to see if a list of users are part of the teachers group. If so, it removes them from it.
     * Also removes the teacher role from the user, if they have it.
     * @param users users to check if they are in the teachers group and have the teacher role.
     */
    public void checkUsersNotInTeachersGroup(Iterable<UserModel> users) {

        for (UserModel user : users) {
            try {
                boolean removedFromGroup = groupModelService.removeUsersFromGroup(new ArrayIterator<>(new UserModel[]{user}), GroupModelServerService.TEACHERS_GROUP_ID);
                if (!removedFromGroup) {
                    logger.error("Something went wrong with the teachers group");
                }
            } catch (InvalidAttributesException e) {
                logger.error("Teachers group does not exist");
            }

            boolean roleWasRemoved = userModelService.checkUserDoesNotHaveTeacherRole(user);
            if (!roleWasRemoved) {
                logger.warn("User {} was not given teacher role. ", user.getUserId());
            }
        }
    }
}
