package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class PermissionServiceTest {

    /**
     * Mocked user response which contains the data of the user
     */
    private final UserResponse mockUser = UserResponse.newBuilder()
            .setBio("default bio")
            .setId(1)
            .setCreated(Timestamp.newBuilder().setSeconds(55))
            .setEmail("hello@test.com")
            .setFirstName("firsttestname")
            .setLastName("lasttestname")
            .setMiddleName("middlettestname")
            .setNickname("niktestname")
            .setPersonalPronouns("He/him")
            .addRoles(UserRole.STUDENT)
            .build();

    private final UserResponse mockUser1 = UserResponse.newBuilder()
            .setBio("default bio")
            .setId(2)
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
    private GroupService groupService;

    @Mock
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // This is required for Mockito annotations to work
    }


    /**
     * Test to check if the user has permission to modify course admin by given current user is a student.
     */
    @Test
    void UserWithStudentRoleTriesToModifyAdmin() {
        String targetRole = "admin";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("student");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertFalse(output);
    }

    /**
     * Test to check if the user has permission to modify course admin by given current user is a teacher.
     */
    @Test
    void UserWithTeacherRoleTriesToModifyAdmin() {
        String targetRole = "admin";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("teacher");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertFalse(output);
    }

    /**
     * Test to check if the user has permission to modify teacher by given current user is a teacher.
     */
    @Test
    void UserWithTeacherRoleTriesToModifyTeacher() {
        String targetRole = "teacher";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("teacher");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertTrue(output);
    }

    /**
     * Test to check if the user has permission to modify course admin by given current user is a course admin.
     */
    @Test
    void UserWithAdminRoleTriesToModifyAdmin() {
        String targetRole = "admin";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("admin");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertTrue(output);
    }

    /**
     * Test to check if the user has permission to modify student by given current user is a course admin.
     */
    @Test
    void UserWithAdminRoleTriesToModifyStudent() {
        String targetRole = "student";
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("admin");
        boolean output = permissionService.isValidToModifyRole(targetRole, mockUser.getId());
        Assertions.assertTrue(output);
    }

    /**
     * Test if current user with student role is able to modify on project page.
     */
    @Test
    void UserWithStudentRoleTriesToModifyProjectPage() {
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("student");
        boolean isValid = permissionService.isValidToModify(mockUser.getId());
        Assertions.assertFalse(isValid);
    }

    /**
     * Test if current user with teacher role is able to modify on project page.
     */
    @Test
    void UserWithTeacherRoleTriesToModifyProjectPage() {
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("teacher");
        boolean isValid = permissionService.isValidToModify(mockUser.getId());
        Assertions.assertTrue(isValid);
    }

    /**
     * Test if current user with admin role is able to modify on project page.
     */
    @Test
    void UserWithAdminRoleTriesToModifyProjectPage() {
        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("admin");
        boolean isValid = permissionService.isValidToModify(mockUser.getId());
        Assertions.assertTrue(isValid);
    }

    /**
     * Test if current user is a student but in the given group, the user is able to modify the group.
     */
    @Test
    void testIsValidToModifyGroupSettingPageWhenInTheGroup() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder()
                .setGroupId(1)
                .addMembers(mockUser)
                .addMembers(mockUser1)
                .build();

        when(registerClientService.getUserData(mockUser.getId())).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("student");
        when(groupService.getGroupDetails(1)).thenReturn(response);

        boolean isValid = permissionService.isValidToModifyGroupSettingPage(1, 1);
        Assertions.assertTrue(isValid);
    }

    /**
     * Test if current user is a student and not in the given group, the user is not able to modify the group.
     */
    @Test
    void testIsValidToModifyGroupSettingPageWhenNotInTheGroup() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder()
                .setGroupId(1)
                .addMembers(mockUser)
                .addMembers(mockUser1)
                .build();

        when(registerClientService.getUserData(3)).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("student");
        when(groupService.getGroupDetails(1)).thenReturn(response);

        boolean isValid = permissionService.isValidToModifyGroupSettingPage(1, 3);
        Assertions.assertFalse(isValid);
    }

    /**
     * Test if current user is an administrator, the user is able to modify the group.
     * Also test if getGroupDetails is not called.
     */
    @Test
    void testIsValidToModifyGroupSettingPageWhenUserIsAdmin() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder()
                .setGroupId(1)
                .addMembers(mockUser)
                .addMembers(mockUser1)
                .build();

        when(registerClientService.getUserData(1)).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("admin");
        when(groupService.getGroupDetails(1)).thenReturn(response);

        boolean isValid = permissionService.isValidToModifyGroupSettingPage(1, 1);

        verify(groupService, times(0)).getGroupDetails(any(Integer.class));

        Assertions.assertTrue(isValid);
    }

    /**
     * Test if current user is a teacher, the user is able to modify the group.
     * Also test if getGroupDetails is not called.
     */
    @Test
    void testIsValidToModifyGroupSettingPageWhenUserIsTeacher() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder()
                .setGroupId(1)
                .addMembers(mockUser)
                .addMembers(mockUser1)
                .build();

        when(registerClientService.getUserData(1)).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("teacher");
        when(groupService.getGroupDetails(1)).thenReturn(response);

        boolean isValid = permissionService.isValidToModifyGroupSettingPage(1, 1);

        verify(groupService, times(0)).getGroupDetails(any(Integer.class));

        Assertions.assertTrue(isValid);
    }

    /**
     * Test if current user is a student but in the given group, the user is able to modify the group.
     * Also test if getGroupDetails is called once.
     */
    @Test
    void testIsValidToModifyGroupSettingPageWhenUserIsStudentButInGroup() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder()
                .setGroupId(1)
                .addMembers(mockUser)
                .addMembers(mockUser1)
                .build();

        when(registerClientService.getUserData(1)).thenReturn(mockUser);
        when(elementService.getUserHighestRole(mockUser)).thenReturn("student");
        when(groupService.getGroupDetails(1)).thenReturn(response);

        boolean isValid = permissionService.isValidToModifyGroupSettingPage(1, 1);

        verify(groupService, times(1)).getGroupDetails(any(Integer.class));

        Assertions.assertTrue(isValid);
    }

}
