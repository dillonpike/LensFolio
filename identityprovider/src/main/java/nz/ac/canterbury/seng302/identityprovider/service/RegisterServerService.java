package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.Database;
import nz.ac.canterbury.seng302.identityprovider.User;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    Database database = new Database();

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {

        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        User user = new User(database, request.getUsername(), request.getPassword());
        boolean wasAdded = user.addUser(request.getFirstName() + " " + request.getLastName(), request.getEmail());

        if (wasAdded) {
            responseObserver.onNext(reply.setNewUserId(user.getId()).build());
            responseObserver.onNext(reply.setIsSuccess(true).build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).build());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {

        UserResponse.Builder reply = UserResponse.newBuilder();

        System.out.println("Got id number: " + request.getId());

        responseObserver.onNext(reply.setUsername("TestUsername123").build());
        responseObserver.onNext(reply.setFirstName("FirstNameTest").build());
        responseObserver.onNext(reply.setLastName("LastNameTest").build());
        responseObserver.onNext(reply.setEmail("test@gmail.com").build());
        responseObserver.onNext(reply.setBio("This is a test bio coming from the IDP").build());
        responseObserver.onNext(reply.setPersonalPronouns("They/Them").build());

        responseObserver.onCompleted();
    }

}
