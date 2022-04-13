package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.util.Set;

import java.util.List;


@GrpcService
public class UserAccountServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    private Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();

    @Autowired
    private UserModelService userModelService;

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
                responseObserver.onNext(reply.setIsSuccess(false).build());
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
            System.err.println("Failed to create and add new user to database");
        }
        if (wasAdded) {
            responseObserver.onNext(reply.setNewUserId(createdUser.getUserId()).setIsSuccess(true).build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).build());
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
                reply
                        .setEmail(user.getEmail())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setMiddleName(user.getMiddleName())
                        .setUsername(user.getUsername())
                        .setNickname(user.getNickname())
                        .setBio(user.getBio())
                        .setPersonalPronouns(user.getPersonalPronouns())
                        .setCreated(user.getDateAdded());
                Set<Roles> roles = user.getRoles();
                Roles[] rolesArray = roles.toArray(new Roles[roles.size()]);

                for(int i = 0; i< rolesArray.length; i++){
                    reply.addRolesValue(rolesArray[i].getId());
//                    System.out.println("=======");
//                    System.out.println(rolesArray[i].getRoleName());
//                    System.out.println(rolesArray[i].getId());
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
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
            wasSaved = userModelService.saveEditedUser(user);
            if(wasSaved){
                reply.setIsSuccess(true).setMessage("User Account is successfully updated!");
            } else {
                reply.setIsSuccess(false).setMessage("Something went wrong");
            }
        } catch(Exception e) {
            System.err.println("User failed to be changed to new values");
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
                wasSaved = userModelService.saveEditedUser(user);
                if(wasSaved){
                    reply.setIsSuccess(true).setMessage("User password successfully updated!");
                } else {
                    reply.setIsSuccess(false).setMessage("Something went wrong");
                }
            } else {
                reply.setIsSuccess(false).setMessage("Current password was incorrect.");
            }
        } catch(Exception e) {
            System.err.println("User failed to be changed to new values");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPaginatedUsers(GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
        PaginatedUsersResponse.Builder reply = PaginatedUsersResponse.newBuilder();
        List<UserModel> allUsers = userModelService.findAllUser();
        for (int i = 0; i < allUsers.size(); i++) {
            reply.addUsers(getUserInfo(allUsers.get(i)));
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    private UserResponse getUserInfo(UserModel user) {
        UserResponse.Builder response = UserResponse.newBuilder();
        response.setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setNickname(user.getNickname());
        return response.build();
    }

}
