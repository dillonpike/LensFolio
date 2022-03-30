package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Service
public class RegisterClientService {

    @GrpcClient(value = "identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();

    /**
     * Creates a UserRegisterRequest and returns the response from the IDP. The IDP will save a new user to the database
     * and whether this worked will be reflected in the response.
     * @param username Username of the new user
     * @param password Password of the new user
     * @param firstName First name of the new user
     * @param middleName Middle name of the new user
     * @param lastName Last name of the new user
     * @param email Email of the new user
     * @return UserRegisterResponse that has the new userId and a conformation of the user being added.
     */
    public UserRegisterResponse receiveConformation(final String username, final String password, final String firstName, final String middleName, final String lastName, final String email) {
        String encodedPassword = encryptPassword(password);
        UserRegisterRequest response = UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(encodedPassword)
                .setFirstName(firstName)
                .setMiddleName(middleName)
                .setLastName(lastName)
                .setEmail(email)
                .build();
        return userAccountStub.register(response);
    }

    /**
     * Gets the users' data from the IDP based on the given userId.
     * @param userId Id of the user wanted
     * @return UserResponse that has the users' information saved in it
     */
    public UserResponse getUserData(final int userId) {
        GetUserByIdRequest response = GetUserByIdRequest.newBuilder().setId(userId).build();
        return userAccountStub.getUserAccountById(response);
    }

    /**
     * Edits a users' data in the IDP based on the given userId.
     * @param userId Id of the user
     * @param firstName New first name of the user
     * @param middleName New middle name of the user
     * @param lastName New last name of the user
     * @param email New email of the user
     * @param bio New bio of the user
     * @param nickname New nickname of the user
     * @param personalPronouns New personal pronouns of the user
     * @return EditUserResponse that holds a conformation that the information was saved successfully
     */
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

    /**
     * Changes the users' password in the IDP based on the given userId.
     * @param userId Id of the user
     * @param currentPassword Old password of the user
     * @param newPassword New password of the user to change it to.
     * @return ChangePasswordResponse that holds a conformation of whether the new password was saved.
     */
    public ChangePasswordResponse changePassword(final int userId, final String currentPassword, final String newPassword) {
        String encodedNewPassword = pbkdf2PasswordEncoder.encode(newPassword);
        ChangePasswordRequest response = ChangePasswordRequest.newBuilder()
                .setUserId(userId)
                .setCurrentPassword(currentPassword)
                .setNewPassword(encodedNewPassword)
                .build();
        return userAccountStub.changeUserPassword(response);
    }

    /**
     * Encrypts the given password and returns it.
     * @param password password to encrypt
     * @return encrypted password
     */
    public String encryptPassword (String password) {
        return pbkdf2PasswordEncoder.encode(password);
    }


}
