package nz.ac.canterbury.seng302.identityprovider.server;

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
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.directory.InvalidAttributesException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles GRPC requests from the portfolio by performing actions related to groups, such as deleting a group, and
 * returns responses.
 */
@GrpcService
public class GroupModelServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    @Autowired
    private GroupModelService groupModelService;

    @Autowired
    private GroupRepository repository;

    @Autowired
    private UserModelService userModelService;

    public static final Integer MEMBERS_WITHOUT_GROUP_ID = 1;

    public static final Integer TEACHERS_GROUP_ID = 2;

    /**
     * Attempts to delete a group with the id in the request. Sends a response with an isSuccess value and message.
     * @param request request that contains a group id
     * @param responseObserver used to send the response
     */
    @Override
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        DeleteGroupResponse.Builder reply = DeleteGroupResponse.newBuilder();

        if (groupModelService.removeGroup(request.getGroupId())) {
            responseObserver.onNext(reply.setIsSuccess(true).setMessage("Successful").build());
        } else {
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
            reply.setIsSuccess(false).setMessage("Name was not unique");
        }
        if (!longNameUnique) {
            ValidationError.Builder error = ValidationError.newBuilder();
            error.setErrorText("Long Name not unique");
            error.setFieldName("longName");
            reply.addValidationErrors(error.build());
            reply.setIsSuccess(false).setMessage("Name was not unique");
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
            }  catch (Exception e) {
                reply.setIsSuccess(false).setMessage("Something went wrong saving the group");
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

            List<UserModel> userModelList = groupModel.getUsers();
            for (UserModel userModel : userModelList) {
                reply.addMembers(userModelService.getUserInfo(userModel));
            }
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
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
            reply.setIsSuccess(false).setMessage("Name was not unique");
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
            reply.setIsSuccess(false).setMessage("Name was not unique");
        }
    }
}
