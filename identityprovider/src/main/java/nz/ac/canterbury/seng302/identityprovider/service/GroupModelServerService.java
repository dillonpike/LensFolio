package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupsServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class GroupModelServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    @Autowired
    private GroupModelService groupService;

    /**
     * Attempts to delete a group with the id in the request. Sends a response with an isSuccess value and message.
     * @param request request that contains a group id
     * @param responseObserver used to send the response
     */
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        DeleteGroupResponse.Builder reply = DeleteGroupResponse.newBuilder();

        if (groupService.removeGroup(request.getGroupId())) {
            responseObserver.onNext(reply.setIsSuccess(true).setMessage("Successful").build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).setMessage("Unsuccessful").build());
        }
        responseObserver.onCompleted();
    }
}
