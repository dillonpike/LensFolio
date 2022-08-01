package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PermissionServiceTest {

    /**
     * Mocked user response which contains the data of the user
     */
    private final UserResponse mockUser = UserResponse.newBuilder()
            .setBio("default bio")
            .setCreated(Timestamp.newBuilder().setSeconds(55))
            .setEmail("hello@test.com")
            .setFirstName("firsttestname")
            .setLastName("lasttestname")
            .setMiddleName("middlettestname")
            .setNickname("niktestname")
            .setPersonalPronouns("He/him")
            .addRoles(UserRole.STUDENT)
            .build();

    /**
     * AuthState object to be used when we mock security context
     */
    public AuthState validAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    public Model model;

    @Autowired
    private PermissionService permissionService = new PermissionService();

    @Autowired
    private ElementService elementService = new ElementService();

    @Autowired
    private RegisterClientService registerClientService = new RegisterClientService();

    @Autowired
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountServiceBlockingStub = mock(UserAccountServiceGrpc.UserAccountServiceBlockingStub.class);

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        registerClientService.userAccountStub = userAccountServiceBlockingStub;
        permissionService.registerClientService = registerClientService;
        permissionService.elementService = elementService;
    }


    @Test
    void UserWithStudentRoleTriesToModifyAdmin() {
        String targetRole = "admin";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);

        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertFalse(output);

    }
}
