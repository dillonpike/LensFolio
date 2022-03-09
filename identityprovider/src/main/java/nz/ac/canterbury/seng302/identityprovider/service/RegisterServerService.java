package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.Database;
import nz.ac.canterbury.seng302.identityprovider.User;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
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
            responseObserver.onNext(reply.setIsSuccess(true).build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).build());
        }

        responseObserver.onCompleted();
    }

}
