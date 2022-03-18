package nz.ac.canterbury.seng302.portfolio.service;


import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class EditAccountClientService {

    @GrpcClient(value = "identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub UserAccountStub;

    public EditUserResponse receiveConformation(final int userId, final String newFullName, final String newNickName, final String newEmail, final String newGender, final String newBio)
    {
//        EditUserRequest response = EditUserRequest.newBuilder()
//                .setUserId(userId)
//                .setFirstName(newFullName)
//                .setLastName("")
//                .setEmail(newEmail)
//                .setPersonalPronouns(newGender)
//                .setBio(newBio)
//                .setNickname(newNickName)
//                .build();
//        return UserAccountStub.register(response);
    return null;
    }
}
