package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

@Service
public class RegisterClientService {

    @GrpcClient(value = "identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    public UserRegisterResponse receiveConformation(final String username, final String password, final String firstName, final String middleName, final String lastName, final String email) {
        UserRegisterRequest response = UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setFirstName(firstName)
                .setMiddleName(middleName)
                .setLastName(lastName)
                .setEmail(email)
                .build();
        return userAccountStub.register(response);
    }

    public UserResponse getUserData(final int userId) {
        GetUserByIdRequest response = GetUserByIdRequest.newBuilder().setId(userId).build();
        return userAccountStub.getUserAccountById(response);
    }

    public EditUserResponse setUserData(final int userId, final String firstName, final String middleName, final String lastName, final String email, final String bio, final String nickname, final String personalPronouns) {
        EditUserRequest response = EditUserRequest.newBuilder()
                .setUserId(userId)
                .setFirstName(firstName)
                .setMiddleName(middleName)
                .setLastName(lastName)
                .setEmail(email)
                .setBio(bio)
                .setNickname(nickname)
                .setPersonalPronouns(personalPronouns)
                .build();
        return userAccountStub.editUser(response);
    }

    public ChangePasswordResponse setPassword(final int userId, final String currentPassword, final String newPassword) {
        ChangePasswordRequest response = ChangePasswordRequest.newBuilder()
                .setUserId(userId)
                .setCurrentPassword(currentPassword)
                .setNewPassword(newPassword)
                .build();
        return userAccountStub.changeUserPassword(response);
    }

}
