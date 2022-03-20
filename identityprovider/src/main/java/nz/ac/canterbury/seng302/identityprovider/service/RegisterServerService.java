package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import jdk.swing.interop.SwingInterOpUtils;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;

import nz.ac.canterbury.seng302.shared.identityprovider.*;

import java.sql.Connection;


@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @Autowired
    private UserModelService userModelService;

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        System.out.println("start server regis");
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        boolean wasAdded = false;
        UserModel newUser = null;

        try {
            // Any empty fields are because you can't add those fields when you create an account initially.
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
            UserModel user = userModelService.addUser(newUser);
            System.out.println(user + "<- Just added");
            System.out.println(user.getNickname() + "<- nickname");
            System.out.println(user.getDateAddedString() + "<- date");
            wasAdded = true;
        } catch (Exception e) {
            System.err.println("Failed to create and add new user to database");
            e.printStackTrace();
        }
        System.out.println(wasAdded + "<= Was added");

        if (wasAdded) {
            responseObserver.onNext(reply.setNewUserId(newUser.getUserId()).build());
            responseObserver.onNext(reply.setIsSuccess(true).build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).build());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {

        UserResponse.Builder reply = UserResponse.newBuilder();

        try {
            UserModel user = userModelService.getUserById(request.getId());
            System.out.println(user);
            reply
                    .setEmail(user.getEmail())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setMiddleName(user.getMiddleName())
                    .setUsername(user.getUsername())
                    .setBio(user.getBio())
                    .setPersonalPronouns(user.getPersonalPronouns())
                    .setCreated(user.getDateAdded());
            System.out.println(user.getDateAddedString() + "<- date register class");
        } catch(Exception e) {
            e.printStackTrace();
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

}

// Code for if queries need to be made to the database directly.
//Connection conn = null;
////        try {
////            conn = DriverManager.getConnection("jdbc:h2:file:./subdirectory/userdb", "sa", "");
////            Statement statement = conn.createStatement();
////            statement.execute("DROP TABLE IF EXISTS User_Model;");
////            statement.execute("CREATE TABLE User_Model (" +
////                    "User_Id int NOT NULL UNIQUE PRIMARY KEY, " +
////                    "Username VARCHAR(30) NOT NULL, " +
////                    "Password VARCHAR(50) NOT NULL, " +
////                    "First_Name VARCHAR(50) NOT NULL, " +
////                    "Middle_Name VARCHAR(50) NOT NULL, " +
////                    "Last_Name VARCHAR(50) NOT NULL, " +
////                    "Nickname VARCHAR(50) DEFAULT NULL, " +
////                    "Email VARCHAR(30) NOT NULL, " +
////                    "Bio VARCHAR(100) DEFAULT NULL," +
////                    "Personal_Pronouns VARCHAR(30) DEFAULT NULL, " +
////                    "Date_Added BINARY VARYING(1000) NOT NULL" +
////                    ");");
////            System.out.println("Yee");
////            conn.close();
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }