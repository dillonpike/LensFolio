package nz.ac.canterbury.seng302.identityprovider.server;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.IdentityProviderApplication;
import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.RolesRepository;
import nz.ac.canterbury.seng302.identityprovider.service.GroupModelService;
import nz.ac.canterbury.seng302.identityprovider.service.UserModelService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.mariadb.jdbc.MariaDbBlob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import javax.naming.directory.InvalidAttributesException;
import java.text.MessageFormat;
import java.util.Set;

import java.util.List;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import static nz.ac.canterbury.seng302.shared.util.FileUploadStatus.*;


@GrpcService
public class UserAccountServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    private Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();

    @Autowired
    private UserModelService userModelService;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private GroupModelService groupModelService;

    @Value("${spring.datasource.url}")
    private String dataSource;

    private static final Logger logger = LoggerFactory.getLogger(UserAccountServerService.class);

    /***
     * Attempts to register a user with a given username, password, first name, middle name, last name, email.
     */
    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        boolean wasAdded = false;
        UserModel newUser;
        UserModel createdUser = null;

        UserModel uniqueUser = userModelService.getUserByUsername(request.getUsername());
        try {
            // Any empty fields are because you can't add those fields when you create an account initially.
            if (uniqueUser != null) {
                responseObserver.onNext(reply.setIsSuccess(false).setMessage("Username taken").build());
                responseObserver.onCompleted();
                return;
            }
            newUser = new UserModel(
                    request.getUsername(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getMiddleName(), //request.getMiddleName(),
                    request.getLastName(), //request.getLastName(),
                    "", //request.getNickname(),
                    request.getEmail(),
                    "Default Bio", //request.getBio(),
                    "Unknown Pronouns" //request.getPersonalPronouns()
            );
            createdUser = userModelService.addUser(newUser);
            wasAdded = true;
        } catch (Exception e) {
            logger.error(MessageFormat.format(
                    "Failed to create and add new user to database: {0}", e.getMessage()));
        }
        if (wasAdded) {
            responseObserver.onNext(reply.setNewUserId(createdUser.getUserId()).setMessage("Successful").setIsSuccess(true).build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).setMessage("Unsuccessful").build());
        }
        responseObserver.onCompleted();
    }

    /***
     * Getter method to get user model with a given user ID
     */
    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {

        UserResponse.Builder reply = UserResponse.newBuilder();
        boolean isExist = userModelService.existsByUserId(request.getId());
        try {
            if (!isExist) {
                reply.setEmail("");
            } else {
                UserModel user = userModelService.getUserById(request.getId());

                // If there isn't a user image it returns an empty string which is then identified by the portfolio.
                // It will then display the default user image.
                String imageDirectory = user.getPhotoDirectory();
                if (imageDirectory == null) {
                    imageDirectory = "";
                }

                reply
                        .setEmail(user.getEmail())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setMiddleName(user.getMiddleName())
                        .setUsername(user.getUsername())
                        .setNickname(user.getNickname())
                        .setBio(user.getBio())
                        .setPersonalPronouns(user.getPersonalPronouns())
                        .setCreated(user.getDateAdded())
                        .setProfileImagePath(imageDirectory);
                Set<Roles> roles = user.getRoles();
                Roles[] rolesArray = roles.toArray(new Roles[roles.size()]);

                for (Roles value : rolesArray) {
                    reply.addRolesValue(value.getId());
                }
            }

        } catch(Exception e) {
            logger.error(e.getMessage());
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void editUser(EditUserRequest request, StreamObserver<EditUserResponse> responseObserver) {
        EditUserResponse.Builder reply = EditUserResponse.newBuilder();

        boolean wasSaved;

        try {
            UserModel currentUser = userModelService.getUserById(request.getUserId());
            UserModel user = new UserModel();
            user.setUserId(request.getUserId());
            user.setBio(request.getBio());
            user.setEmail(request.getEmail());
            user.setNickname(request.getNickname());
            user.setFirstName(request.getFirstName());
            user.setMiddleName(request.getMiddleName());
            user.setLastName(request.getLastName());
            user.setPersonalPronouns(request.getPersonalPronouns());
            user.setUsername(currentUser.getUsername());
            user.setPassword(currentUser.getPassword());
            user.setDateAdded(currentUser.getDateAdded());
            user.setRoles(currentUser.getRoles());
            user.setPhotoDirectory(currentUser.getPhotoDirectory());
            wasSaved = userModelService.saveEditedUser(user);
            if (wasSaved) {
                reply.setIsSuccess(true).setMessage("User Account is successfully updated!");
            } else {
                reply.setIsSuccess(false).setMessage("Something went wrong");
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format(
                    "User failed to be changed to new values: {0}", e.getMessage()));
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();

    }

    @Override
    public void changeUserPassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> responseObserver) {
        ChangePasswordResponse.Builder reply = ChangePasswordResponse.newBuilder();

        boolean wasSaved;
        try {
            UserModel currentUser = userModelService.getUserById(request.getUserId());
            if (pbkdf2PasswordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
                UserModel user = new UserModel();
                user.setUserId(request.getUserId());
                user.setBio(currentUser.getBio());
                user.setEmail(currentUser.getEmail());
                user.setNickname(currentUser.getNickname());
                user.setFirstName(currentUser.getFirstName());
                user.setMiddleName(currentUser.getMiddleName());
                user.setLastName(currentUser.getLastName());
                user.setPersonalPronouns(currentUser.getPersonalPronouns());
                user.setUsername(currentUser.getUsername());
                user.setPassword(request.getNewPassword());
                user.setDateAdded(currentUser.getDateAdded());
                user.setRoles(currentUser.getRoles());
                user.setPhotoDirectory(currentUser.getPhotoDirectory());
                wasSaved = userModelService.saveEditedUser(user);
                if (wasSaved) {
                    reply.setIsSuccess(true).setMessage("User password successfully updated!");
                } else {
                    reply.setIsSuccess(false).setMessage("Something went wrong");
                }
            } else {
                reply.setIsSuccess(false).setMessage("Current password was incorrect.");
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format(
                    "User failed to be changed to new values: {0}", e.getMessage()));
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }


    /**
     * Saves the new profile photo to the user using bi-directional streams
     * @param responseObserver for telling the portfolio method the current status
     * @return A Stream Observer that saves photo data as it is given
     */
    @Override
    public StreamObserver<UploadUserProfilePhotoRequest> uploadUserProfilePhoto(StreamObserver<FileUploadStatusResponse> responseObserver) {

        return new StreamObserver<UploadUserProfilePhotoRequest>() {
            ByteArrayOutputStream imageArray = new ByteArrayOutputStream();
            FileUploadStatus fileUploadStatus = PENDING;
            boolean byteFailed = false;
            String message = "Byte uploading";
            int userId;
            String fileType;

            @Override
            public void onNext(UploadUserProfilePhotoRequest value) {
                FileUploadStatusResponse.Builder reply = FileUploadStatusResponse.newBuilder();

                if (value.hasMetaData()) {
                    userId = value.getMetaData().getUserId();
                    fileType = value.getMetaData().getFileType();
                    responseObserver.onNext(reply.setStatus(fileUploadStatus).setMessage("Got Metadata").build());
                } else {
                    try {
                        imageArray.write(value.getFileContent().toByteArray());
                        fileUploadStatus = IN_PROGRESS;
                        message = "Bytes uploading";
                        responseObserver.onNext(reply.setStatus(fileUploadStatus).setMessage(message).build());
                    } catch (IOException e) {
                        fileUploadStatus = FAILED;
                        message = "Bytes failed to write to OutputStream";
                        byteFailed = true;
                        responseObserver.onNext(reply.setStatus(fileUploadStatus).setMessage(message).build());
                        logger.error(e.getMessage());
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Failed to stream image:");
                logger.error(t.getMessage());
            }

            @Override
            public void onCompleted() {
                FileUploadStatusResponse.Builder reply = FileUploadStatusResponse.newBuilder();

                //  Call the function savePhotoToUser to save the photo
                //  In the future, this could be kept as an array, rather than converting to a blob.
                Blob blob = new MariaDbBlob(imageArray.toByteArray());
                boolean wasSaved = false;
                if (!byteFailed) {
                    wasSaved = savePhotoToUser(userId, blob);
                }

                if (wasSaved) {
                    message = "Image saved to database";
                    fileUploadStatus = SUCCESS;
                } else {
                    message = "Image failed to save to database";
                    fileUploadStatus = FAILED;
                }

                responseObserver.onNext(reply.setStatus(fileUploadStatus).setMessage(message).build());
                responseObserver.onCompleted();
            }
        };
    }


    /**
     * Saves a photo locally to the IDP and then its pathway to a user in the database. Overwrites anything already saved.
     * @param userId    ID of the user you want to add the photo to
     * @param photo     Photo blob for saving
     * @return Whether the new photo was saved
     */
    private boolean savePhotoToUser(int userId, Blob photo) {
        boolean status;

        UserModel user = userModelService.getUserById(userId);
        if (user != null) { // Valid user.
            String directory = MessageFormat.format("{0}/{1}/{2}/public/",
                    IdentityProviderApplication.IMAGE_DIR, getApplicationLocation(dataSource), userId);
            String profileImagePath;

            if (!new File(directory).mkdirs()) { // Ensures folders are made.
                logger.warn("Not all folders may have been created.");
            }

            File imageFile = new File(directory + "/profileImage");
            try (FileOutputStream imageOutput = new FileOutputStream(imageFile)) {

                if  (photo != null) { // Checks to ensure the photo given from the Portfolio exists.
                    imageOutput.write(photo.getBytes(1, (int) photo.length()));
                    profileImagePath = imageFile.getAbsolutePath();
                } else {
                    // This means just the default image will be used by the portfolio when retrieved.
                    profileImagePath = null;
                }
            } catch(Exception e) { // Error saving image in IDP or uploading information to the database.
                logger.error(MessageFormat.format(
                        "Something went wrong saving the users photo: {0}", e.getMessage()));
                return false;
            }

            // Updates user.
            user.setPhotoDirectory(profileImagePath);
            status = userModelService.saveEditedUser(user);

        } else { // Invalid user.
            status = false;
        }

        return status;
    }

    /**
     * Gets the location of which branch/vm the program is running on.
     * @param dataSource    This relates to the applications property file being used.
     * @return 'dev', 'test' or 'prod', depending on the branch/vm.
     */
    private static String getApplicationLocation(String dataSource) {
        if (dataSource.contains("seng302-2022-team100")) {
            if (dataSource.contains("test")) {
                return "test";
            } else {
                return "prod";
            }
        }
        return "dev";
    }

    /**
     * Deletes a users image pathway from the database (Sets it to null).
     * @param request   Holds the user ID
     * @param responseObserver Tells the portfolio the status of whether the photo was deleted
     */
    @Override
    public void deleteUserProfilePhoto(DeleteUserProfilePhotoRequest request, StreamObserver<DeleteUserProfilePhotoResponse> responseObserver) {
        DeleteUserProfilePhotoResponse.Builder reply = DeleteUserProfilePhotoResponse.newBuilder();

        boolean wasDeleted = false;
        String message = "Photo failed to delete";

        try {
            UserModel user = userModelService.getUserById(request.getUserId());
            if (user != null) {
                user.setPhotoDirectory(null);
                wasDeleted = userModelService.saveEditedUser(user);
                message = "Photo deleted successfully";
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format(
                    "Something went wrong deleting the users photo: {0}", e.getMessage()));
        }

        reply.setIsSuccess(wasDeleted);
        reply.setMessage(message);
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /***
     * Method to communicate with IDP to get all users from database
     * @param request GetPaginatedUsersRequest
     * @param responseObserver
     */
    @Override
    public void getPaginatedUsers(GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
        PaginatedUsersResponse.Builder reply = PaginatedUsersResponse.newBuilder();
        List<UserModel> allUsers = userModelService.findAllUser();
        for (UserModel allUser : allUsers) {
            reply.addUsers(userModelService.getUserInfo(allUser));
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /***
     * Helper method to build the UserRoleChangeResponse which the respond of adding role to user process
     * @param request ModifyRoleOfUserRequest the request which contains the role that will be added to the user
     * @return UserRoleChangeResponse which contains information whether adding a role to user was done successfully or not
     */
    @VisibleForTesting
    UserRoleChangeResponse addRoleToUserHelper (ModifyRoleOfUserRequest request) {
        UserRoleChangeResponse.Builder reply = UserRoleChangeResponse.newBuilder();
        try {
            UserModel user = userModelService.getUserById(request.getUserId());
            UserRole role = request.getRole();
            if (user != null) {
                if (role.getNumber() == 0) {
                    Roles studentRole = rolesRepository.findByRoleName("STUDENT");
                    user.addRoles(studentRole);
                    userModelService.saveEditedUser(user);
                } else if (role.getNumber() == 1) {
                    Roles studentRole = rolesRepository.findByRoleName("TEACHER");
                    user.addRoles(studentRole);
                    userModelService.saveEditedUser(user);
//                    boolean wasAddedToGroup = groupModelService.addUserToGroup(user.getUserId(), GroupModelServerService.TEACHERS_GROUP_ID);
//                    if (!wasAddedToGroup) {
//                        throw new InvalidAttributesException("User or Teacher Group did not exist. Or user already part of the Teachers group. ");
//                    }
                } else if (role.getNumber() == 2) {
                    Roles studentRole = rolesRepository.findByRoleName("COURSE ADMINISTRATOR");
                    user.addRoles(studentRole);
                    userModelService.saveEditedUser(user);
                }
            }
            reply.setIsSuccess(true);
            return reply.build();
        } catch (Exception e) {
            logger.error(MessageFormat.format(
                    "Something went wrong: {0}", e.getMessage()));
            reply.setIsSuccess(false);
            return reply.build();
        }
    }

    /***
     * Add role to a user
     * Call helper function addRoleToUserHelper() to build the respond
     * @param request ModifyRoleOfUserRequest the request which contains the role that will be added to the user
     */
    @Override
    public void addRoleToUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {
        UserRoleChangeResponse reply = addRoleToUserHelper(request);

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /***
     * Delete a role from a user
     * Call helper function removeRoleFromUserHelper() to build the respond
     * @param request ModifyRoleOfUserRequest the request which contains the role that will be deleted from the user
     */
    @Override
    public void removeRoleFromUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {

        UserRoleChangeResponse reply = removeRoleFromUserHelper(request);

        responseObserver.onNext(reply);
        responseObserver.onCompleted();

    }

    /***
     * Helper method to build the UserRoleChangeResponse which the respond of deleting role from user process
     * @param request ModifyRoleOfUserRequest the request which contains the role that will be deleted from the user
     * @return UserRoleChangeResponse which contains information whether deleting a role to user was done successfully or not
     */
    @VisibleForTesting
    UserRoleChangeResponse removeRoleFromUserHelper (ModifyRoleOfUserRequest request) {
        UserRoleChangeResponse.Builder reply = UserRoleChangeResponse.newBuilder();
        try {
            UserModel user = userModelService.getUserById(request.getUserId());
            UserRole role = request.getRole();
            if (user != null) {
                if (role.getNumber() == 0) {
                    Roles studentRole = rolesRepository.findByRoleName("STUDENT");
                    user.deleteRole(studentRole);
                    userModelService.saveEditedUser(user);
                    reply.setIsSuccess(true);
                }else if (role.getNumber() == 1) {
                    Roles teacherRole = rolesRepository.findByRoleName("TEACHER");
                    user.deleteRole(teacherRole);
                    userModelService.saveEditedUser(user);
//                    boolean wasRemovedFromGroup = groupModelService.removeUserFromGroup(user.getUserId(), GroupModelServerService.TEACHERS_GROUP_ID);
//                    if (!wasRemovedFromGroup) {
//                        throw new InvalidAttributesException("User or Teacher Group did not exist. Or user already not part of the Teachers group. ");
//                    }
                    reply.setIsSuccess(true);
                } else {
                    Roles adminRole = rolesRepository.findByRoleName("COURSE ADMINISTRATOR");
                    user.deleteRole(adminRole);
                    userModelService.saveEditedUser(user);
                    reply.setIsSuccess(true);
                }
            }
            return reply.build();
        } catch (Exception e) {
            logger.error(MessageFormat.format(
                    "Something went wrong: {0}", e.getMessage()));
            reply.setIsSuccess(false);
            return reply.build();
        }

    }

}
