package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

@Service
public class RegisterClientService {

    @GrpcClient(value = "identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    public UserRegisterResponse receiveConformation(final String username, final String password, final String firstName, final String lastName, final String email) {
        UserRegisterRequest response = UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .build();
        return userAccountStub.register(response);
    }

    public UserResponse getUserData(final int userId) {
        GetUserByIdRequest response = GetUserByIdRequest.newBuilder().setId(userId).build();
        return userAccountStub.getUserAccountById(response);
    }
}
