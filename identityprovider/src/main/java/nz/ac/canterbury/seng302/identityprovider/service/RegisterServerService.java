package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @Autowired
    private UserModelService userModelService;

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {

        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        boolean wasAdded = false;

        try {
            UserModel newUser = new UserModel(
                    request.getUsername(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getMiddleName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getBio(),
                    request.getPersonalPronouns()
            );
            wasAdded = true;
        } catch (Exception e) {
            System.err.println("Failed to create and add new user to database");
            e.printStackTrace();
        }



        if (wasAdded) {
            responseObserver.onNext(reply.setIsSuccess(true).build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).build());
        }

        responseObserver.onCompleted();
    }

}
