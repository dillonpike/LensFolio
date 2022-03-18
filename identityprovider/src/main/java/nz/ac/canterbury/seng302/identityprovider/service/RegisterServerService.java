package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
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

@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @Autowired
    private UserModelService userModelService;

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:h2:file:./subdirectory/userdb", "sa", "");
            Statement statement = conn.createStatement();
            statement.execute("DROP TABLE IF EXISTS User_Model;");
            statement.execute("CREATE TABLE User_Model (" +
                    "User_Id int NOT NULL UNIQUE PRIMARY KEY, " +
                    "Username VARCHAR(30) NOT NULL, " +
                    "Password VARCHAR(50) NOT NULL, " +
                    "First_Name VARCHAR(50) NOT NULL, " +
                    "Middle_Name VARCHAR(50) NOT NULL, " +
                    "Last_Name VARCHAR(50) NOT NULL, " +
                    "Email VARCHAR(30) NOT NULL, " +
                    "Bio VARCHAR(100) DEFAULT NULL," +
                    "Personal_Pronouns VARCHAR(30) DEFAULT NULL" +
                    ");");
            System.out.println("Yee");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        boolean wasAdded = false;

        try {
            UserModel newUser = new UserModel(
                    2,
                    request.getUsername(),
                    request.getPassword(),
                    request.getFirstName(),
                    "a", //request.getMiddleName(),
                    "b", //request.getLastName(),
                    request.getEmail(),
                    "Default Bio", //request.getBio(),
                    "Unknown Pronouns" //request.getPersonalPronouns()
            );
            userModelService.addUser(newUser);
            wasAdded = true;
        } catch (Exception e) {
            System.err.println("Failed to create and add new user to database");
            e.printStackTrace();
        }



        if (wasAdded) {
            responseObserver.onNext(reply.setIsSuccess(true).build());
        } else {
            responseObserver.onNext(reply.setIsSuccess(false).build());
        }

        responseObserver.onCompleted();
    }

}
