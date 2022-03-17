package nz.ac.canterbury.seng302.portfolio.service;


import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class EditAccountServerService {

    @GrpcClient(value = "identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub UserAccountStub;

//    public EditUserResponse receiveConformation(final String newFullName, final String newNickName, final String newEmail, final String newGender, final String newBio)
//    {
//        EditUserRequest response = EditUserRequest.newBuilder()
//                .setNew
//    }
}
