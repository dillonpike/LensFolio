package nz.ac.canterbury.seng302.identityprovider.server;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
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
    private UserModelService userModelService;

    private Integer memberWithoutGroupID = 1;

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
        reply.setGroupId(memberWithoutGroupID);
        Set<Integer> userIDs = new HashSet<>();
        GroupModel groupModel;
        try {
            userIDs = groupModelService.getMembersOfGroup(memberWithoutGroupID);
            groupModel = groupModelService.getGroupById(memberWithoutGroupID);
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




}
