package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import jdk.swing.interop.SwingInterOpUtils;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import nz.ac.canterbury.seng302.identityprovider.Database;
import nz.ac.canterbury.seng302.identityprovider.User;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @Autowired
    private UserModelService userModelService;

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {

        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        boolean wasAdded = false;
        UserModel newUser = null;

        try {
            newUser = new UserModel(
                    request.getUsername(),
                    request.getPassword(),
                    request.getFirstName(),
                    "DEFAULTmiddlename", //request.getMiddleName(),
                    "DEFAULTlastname", //request.getLastName(),
                    request.getEmail(),
                    "Default Bio", //request.getBio(),
                    "Unknown Pronouns" //request.getPersonalPronouns()
            );
            UserModel user = userModelService.addUser(newUser);
            System.out.println(user + "<- Just added");
            wasAdded = true;
        } catch (Exception e) {
            System.err.println("Failed to create and add new user to database");
        }
        System.out.println(wasAdded + "<= Was added");

        if (wasAdded) {
            responseObserver.onNext(reply.setNewUserId(newUser.getUserId()).build());
            responseObserver.onNext(reply.setNewUserId(user.getId()).build());
            responseObserver.onNext(reply.setIsSuccess(true).build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).build());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {

        UserResponse.Builder reply = UserResponse.newBuilder();

        System.out.println("Got id number: " + request.getId());

        responseObserver.onNext(reply.setUsername("TestUsername123").build());
        responseObserver.onNext(reply.setFirstName("FirstNameTest").build());
        responseObserver.onNext(reply.setLastName("LastNameTest").build());
        responseObserver.onNext(reply.setEmail("test@gmail.com").build());
        responseObserver.onNext(reply.setBio("This is a test bio coming from the IDP").build());
        responseObserver.onNext(reply.setPersonalPronouns("They/Them").build());

        responseObserver.onCompleted();
    }

}

// Code for if queries need to be made to the database directly.
//    Connection conn = null;
//        try {
//                conn = DriverManager.getConnection("jdbc:h2:file:./subdirectory/userdb", "sa", "");
//                Statement statement = conn.createStatement();
//                statement.execute("DROP TABLE IF EXISTS User_Model;");
//                statement.execute("CREATE TABLE User_Model (" +
//                "User_Id int NOT NULL UNIQUE PRIMARY KEY, " +
//                "Username VARCHAR(30) NOT NULL, " +
//                "Password VARCHAR(50) NOT NULL, " +
//                "First_Name VARCHAR(50) NOT NULL, " +
//                "Middle_Name VARCHAR(50) NOT NULL, " +
//                "Last_Name VARCHAR(50) NOT NULL, " +
//                "Email VARCHAR(30) NOT NULL, " +
//                "Bio VARCHAR(100) DEFAULT NULL," +
//                "Personal_Pronouns VARCHAR(30) DEFAULT NULL" +
//                ");");
//                System.out.println("Yee");
//                conn.close();
//                } catch (SQLException e) {
//                e.printStackTrace();
//                }