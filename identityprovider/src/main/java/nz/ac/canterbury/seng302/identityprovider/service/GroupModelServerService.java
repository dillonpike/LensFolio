package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles GRPC requests from the portfolio by performing actions related to groups, such as deleting a group, and
 * returns responses.
 */
@GrpcService
public class GroupModelServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    @Autowired
    private GroupModelService GroupModelService;

    /**
     * Attempts to delete a group with the id in the request. Sends a response with an isSuccess value and message.
     * @param request request that contains a group id
     * @param responseObserver used to send the response
     */
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        DeleteGroupResponse.Builder reply = DeleteGroupResponse.newBuilder();

        if (GroupModelService.removeGroup(request.getGroupId())) {
            responseObserver.onNext(reply.setIsSuccess(true).setMessage("Successful").build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).setMessage("Unsuccessful").build());
        }
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

        boolean shortNameUnique = GroupModelService.checkShortNameIsUniqueEditing(request.getGroupId(), request.getShortName());
        boolean longNameUnique = GroupModelService.checkLongNameIsUniqueEditing(request.getGroupId(), request.getLongName());

        addShortNameValidationError(shortNameUnique, reply);
        addLongNameValidationError(longNameUnique, reply);

        if (shortNameUnique && longNameUnique) {
            try {
                if (GroupModelService.editGroup(request.getGroupId(), request.getShortName(), request.getLongName())) {
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
