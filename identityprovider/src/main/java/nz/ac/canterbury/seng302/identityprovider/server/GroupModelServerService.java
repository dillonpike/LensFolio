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

@GrpcService
public class GroupModelServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    @Autowired
    private GroupModelService groupService;

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

        if (groupService.removeGroup(request.getGroupId())) {
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
            userIDs = groupService.getMembersOfGroup(memberWithoutGroupID);
            groupModel = groupService.getGroupById(memberWithoutGroupID);
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
        if (!groupService.checkShortNameIsUnique(request.getShortName())) {
            ValidationError.Builder error = ValidationError.newBuilder();
            error.setErrorText("Short Name not unique");
            error.setFieldName("shortName");
            reply.setValidationErrors(0, error.build());
            reply.setIsSuccess(false).setMessage("Short Name was not unique");
        } else if (!groupService.checkLongNameIsUnique(request.getLongName())) {
            ValidationError.Builder error = ValidationError.newBuilder();
            error.setErrorText("Long Name not unique");
            error.setFieldName("longName");
            reply.setValidationErrors(1, error.build());
            reply.setIsSuccess(false).setMessage("Long Name was not unique");
        } else {
            try {
                GroupModel group = groupService.addGroup(request.getShortName(), request.getLongName(), 0);
                reply.setIsSuccess(true).setMessage("Group created").setNewGroupId(group.getGroupId());
            } catch (Exception e) {
                reply.setIsSuccess(false).setMessage("Something went wrong creating the group");
            }
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }




}
