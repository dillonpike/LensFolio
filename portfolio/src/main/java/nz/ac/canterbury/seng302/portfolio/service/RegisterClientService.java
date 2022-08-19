package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;

/***
 * Contains methods to communicate with Idp(identity provider)
 */
@Service
public class RegisterClientService {

    @GrpcClient(value = "identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    @GrpcClient(value = "identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceStub userAccountNonBlockingStub;

    Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();

    private static final Logger logger = LoggerFactory.getLogger(RegisterClientService.class);

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
    public UserRegisterResponse receiveConformation(final String username,
                                                    final String password,
                                                    final String firstName,
                                                    final String middleName,
                                                    final String lastName,
                                                    final String email,
                                                    final String bio,
                                                    final String pronouns,
                                                    final String alias) {
        String encodedPassword = encryptPassword(password);
        UserRegisterRequest response = UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(encodedPassword)
                .setFirstName(firstName)
                .setMiddleName(middleName)
                .setLastName(lastName)
                .setBio(bio)
                .setPersonalPronouns(pronouns)
                .setNickname(alias)
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
    public String encryptPassword(String password) {
        return pbkdf2PasswordEncoder.encode(password);
    }



    /**
     * Uploads a new profile photo to the idp using a bi-directional stream connection.
     * @param userId Id of the user that is having its photo changed
     * @param imageFile File object of new image
     */
    public boolean uploadUserProfilePhoto(int userId, File imageFile) {

        byte[] imageArray = new byte[0];
        boolean imageFoundCorrectly = true;
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
            ImageReader reader = null;
            while (imageReaders.hasNext()) {
                reader = imageReaders.next();
            }
            if (reader == null) {
                throw new NullPointerException("Reader is empty (null)");
            }

            BufferedImage testImage = ImageIO.read(imageFile);  // DEBUGGING Use imageFile instead
            ByteArrayOutputStream imageArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(testImage, reader.getFormatName(), imageArrayOutputStream);
            imageArray = imageArrayOutputStream.toByteArray();
        } catch (IOException | NullPointerException e) {
            logger.error("You didn't find the image correctly:");
            logger.error(e.getMessage());
            imageFoundCorrectly = false;
        }
        byte[] finalImageArray = imageArray;

        StreamObserver<FileUploadStatusResponse> responseObserver = new StreamObserver<FileUploadStatusResponse>() {
            @Override
            public void onNext(FileUploadStatusResponse value) {

                switch (value.getStatusValue()) {
                    case 0 ->  // PENDING
                            logger.info("Server pending");
                    case 1 ->  // IN_PROGRESS
                            logger.info("Server uploading");
                    case 2 ->  // SUCCESS
                            logger.info("Server finished successfully");
                    case 3 ->  // FAILED
                            logger.error("Server failed to upload image");
                    default -> // ERROR
                            logger.error("Invalid statue code!");
                }
                logger.info("System returned: ");
                logger.info(value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Failed to stream image:");
                logger.error(t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("<-> Finished <->");
            }
        };

        if (imageFoundCorrectly) {
            StreamObserver<UploadUserProfilePhotoRequest> requestObserver = userAccountNonBlockingStub.uploadUserProfilePhoto(responseObserver);
            try {
                ImageInputStream iis = ImageIO.createImageInputStream(imageFile);

                Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

                ImageReader reader = null;
                while (imageReaders.hasNext()) {
                    reader = imageReaders.next();
                }

                if (reader == null) {
                    throw new NullPointerException("Reader is empty (null)");
                }

                // Start with uploading the metadata
                UploadUserProfilePhotoRequest.Builder replyMetaData = UploadUserProfilePhotoRequest.newBuilder();
                ProfilePhotoUploadMetadata.Builder metaData = ProfilePhotoUploadMetadata.newBuilder().setUserId(userId).setFileType(reader.getFormatName());
                replyMetaData.setMetaData(metaData.build());
                requestObserver.onNext(replyMetaData.build());
                // Loop through the bytes
                UploadUserProfilePhotoRequest.Builder reply = UploadUserProfilePhotoRequest.newBuilder();
                reply.setFileContent(ByteString.copyFrom(finalImageArray));
                requestObserver.onNext(reply.build());
                // Complete conversation
                requestObserver.onCompleted();
                return true;
            } catch (Exception e) {
                logger.error(MessageFormat.format(
                        "Something went wrong uploading the file: {0}", e.getMessage()));
                return false;
            }
        }
        return false;
    }

    /**
     * Sends a message through GRPC contracts to delete the accounts image path from the database.
     * @param userId    The users ID.
     * @return The response from the IDP.
     */
    public DeleteUserProfilePhotoResponse deleteUserProfilePhoto(int userId) {
        DeleteUserProfilePhotoRequest.Builder request = DeleteUserProfilePhotoRequest.newBuilder();
        return userAccountStub.deleteUserProfilePhoto(request.setUserId(userId).build());
    }
}
