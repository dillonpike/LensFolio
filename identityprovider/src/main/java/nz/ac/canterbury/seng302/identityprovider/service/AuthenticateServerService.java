package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.authentication.JwtTokenUtil;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc.AuthenticationServiceImplBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

@GrpcService
public class AuthenticateServerService extends AuthenticationServiceImplBase {

    private Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();

    private final int VALID_USER_ID = 1;
    private final String VALID_USER = "abc123";
    private final String VALID_PASSWORD = "Password123!";
    private final String FIRST_NAME_OF_USER = "Valid";
    private final String LAST_NAME_OF_USER = "User";
    private final String FULL_NAME_OF_USER = FIRST_NAME_OF_USER + " " + LAST_NAME_OF_USER;
    private final String ROLE_OF_USER = "teacher"; // Puce teams may want to change this to "teacher" to test some functionality

    private JwtTokenUtil jwtTokenService = JwtTokenUtil.getInstance();

    @Autowired
    private UserModelService userModelService;
    /**
     * Attempts to authenticate a user with a given username and password.
     */
    @Override
    public void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> responseObserver) {
        AuthenticateResponse.Builder reply = AuthenticateResponse.newBuilder();

        UserModel user = userModelService.getUserByUsername(request.getUsername());
        if (user == null) {
            reply
                    .setMessage("Log in attempt failed: username incorrect")
                    .setSuccess(false)
                    .setToken("");
        } else if (user.getUsername().equals(request.getUsername()) && pbkdf2PasswordEncoder.matches( request.getPassword(),  user.getPassword())) {
            String token = jwtTokenService.generateTokenForUser(user.getUsername(), user.getUserId(),
                    user.getFirstName() + user.getMiddleName() + user.getLastName(), userModelService.getHighestRole(user));
            reply
                    .setEmail(user.getEmail())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setMessage("Logged in successfully!")
                    .setSuccess(true)
                    .setToken(token)
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername());
        } else {
            reply
                    .setMessage("Log in attempt failed: password incorrect")
                    .setSuccess(false)
                    .setToken("");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * The AuthenticationInterceptor already handles validating the authState for us, so here we just need to
     * retrieve that from the current context and return it in the gRPC body
     */
    @Override
    public void checkAuthState(Empty request, StreamObserver<AuthState> responseObserver) {
        responseObserver.onNext(AuthenticationServerInterceptor.AUTH_STATE.get());
        responseObserver.onCompleted();
    }
}
