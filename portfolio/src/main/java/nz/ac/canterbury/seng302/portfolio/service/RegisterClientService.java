package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.GreeterGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.stereotype.Service;

@Service
public class RegisterClientService {

    @GrpcClient(value = "identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub UserAccountStub;

    public UserRegisterResponse receiveConformation(final String username, final String password, final String fullName, final String email) {
        UserRegisterRequest response = UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setFirstName(fullName)
                .setLastName("")
                .setEmail(email)
                .build();
        return UserAccountStub.register(response);
    }

}
