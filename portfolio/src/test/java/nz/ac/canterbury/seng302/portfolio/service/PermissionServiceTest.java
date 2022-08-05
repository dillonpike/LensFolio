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
import org.mockito.MockitoAnnotations;
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

    public Model model;

    @InjectMocks
    private PermissionService permissionService;

    @Mock
    private ElementService elementService;

    @Mock
    private RegisterClientService registerClientService;

    @Mock
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // This is required for Mockito annotations to work
    }


    @Test
    void UserWithStudentRoleTriesToModifyAdmin() {
        String targetRole = "admin";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("student");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertFalse(output);
    }

    @Test
    void UserWithTeacherRoleTriesToModifyAdmin() {
        String targetRole = "admin";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("teacher");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertFalse(output);
    }

    @Test
    void UserWithTeacherRoleTriesToModifyTeacher() {
        String targetRole = "teacher";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("teacher");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertTrue(output);
    }

    @Test
    void UserWithAdminRoleTriesToModifyAdmin() {
        String targetRole = "admin";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("admin");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertTrue(output);
    }

    @Test
    void UserWithAdminRoleTriesToModifyStudent() {
        String targetRole = "student";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("admin");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertTrue(output);
    }

    @Test
    void UserWithStudentRoleTriesToModifyProjectPage() {
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("student");
        boolean isValid = permissionService.isValidToModifyProjectPage(mockUser.getId());
        Assertions.assertFalse(isValid);
    }

    @Test
    void UserWithTeacherRoleTriesToModifyProjectPage() {
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("teacher");
        boolean isValid = permissionService.isValidToModifyProjectPage(mockUser.getId());
        Assertions.assertTrue(isValid);
    }

    @Test
    void UserWithAdminRoleTriesToModifyProjectPage() {
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("admin");
        boolean isValid = permissionService.isValidToModifyProjectPage(mockUser.getId());
        Assertions.assertTrue(isValid);
    }
}
