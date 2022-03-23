package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

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
        System.out.println(personalPronouns);
        return userAccountStub.editUser(response);
    }

    public String encryptPassword (String password) {
        Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();
        String cryptedPassword = pbkdf2PasswordEncoder.encode(password);
//        boolean passwordIsValid = pbkdf2PasswordEncoder.matches("lhp20010308", pbkdf2CryptedPassword);
        return cryptedPassword;
    }

}
