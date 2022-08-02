package nz.ac.canterbury.seng302.identityprovider.server;


import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.RolesRepository;
import nz.ac.canterbury.seng302.identityprovider.server.UserAccountServerService;
import nz.ac.canterbury.seng302.identityprovider.service.UserModelService;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyRoleOfUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/***
 * Testing class which contains unit test for methods in UserAccountServerService
 */
class UserAccountServerServiceTest {

    @Mock
    private UserModelService userModelService;

    @Mock
    private RolesRepository rolesRepository;

    @InjectMocks
    UserAccountServerService userAccountServerService = Mockito.spy(UserAccountServerService.class);

    private UserModel userModel = new UserModel("test","test","password","middle","last","nickname","email@email.com","default bio","He/him");

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this); // This is required for Mockito annotations to work
    }

    /***
     * Test the method to add role to user
     * Given te added role is Student
     * Verify that the new role is saved to the database
     * Expect the return contains success information
     */
    @Test
    void addStudentRoleToUser() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder()
                .setRole(UserRole.STUDENT).setUserId(1).build();

        UserModel mockUserModel = Mockito.mock(UserModel.class);
        Roles theRole = new Roles();
        theRole.setRoleName("STUDENT");
        theRole.setId(0);
        when(rolesRepository.findByRoleName("STUDENT")).thenReturn(theRole);
        when(userModelService.getUserById(1)).thenReturn(mockUserModel);
        when(userModelService.saveEditedUser(any(UserModel.class))).thenReturn(true);
        doNothing().when(mockUserModel).addRoles(any(Roles.class));

        ArgumentCaptor<Roles> roleAdded = ArgumentCaptor.forClass(Roles.class);
        ArgumentCaptor<UserModel> addedUserModel = ArgumentCaptor.forClass(UserModel.class);

        UserRoleChangeResponse reply = userAccountServerService.addRoleToUserHelper(request);
        Assertions.assertEquals(true,reply.getIsSuccess());
        Mockito.verify(mockUserModel).addRoles(roleAdded.capture());
        Mockito.verify(userModelService).saveEditedUser(addedUserModel.capture());
    }

    /***
     * Test the method to add role to user
     * Given te added role is Teacher
     * Verify that the new role is saved to the database
     * Expect the return contains success information
     */
    @Test
    void addTeacherRoleToUser() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder()
                .setRole(UserRole.STUDENT).setUserId(1).build();

        UserModel mockUserModel = Mockito.mock(UserModel.class);
        Roles theRole = new Roles();
        theRole.setRoleName("TEACHER");
        theRole.setId(0);
        when(rolesRepository.findByRoleName("TEACHER")).thenReturn(theRole);
        when(userModelService.getUserById(1)).thenReturn(mockUserModel);
        when(userModelService.saveEditedUser(any(UserModel.class))).thenReturn(true);
        doNothing().when(mockUserModel).addRoles(any(Roles.class));

        ArgumentCaptor<Roles> roleAdded = ArgumentCaptor.forClass(Roles.class);
        ArgumentCaptor<UserModel> addedUserModel = ArgumentCaptor.forClass(UserModel.class);

        UserRoleChangeResponse reply = userAccountServerService.addRoleToUserHelper(request);
        Assertions.assertEquals(true,reply.getIsSuccess());
        Mockito.verify(mockUserModel).addRoles(roleAdded.capture());
        Mockito.verify(userModelService).saveEditedUser(addedUserModel.capture());
    }

    /***
     * Test the method to add role to user
     * Given te added role is Admin
     * Verify that the new role is saved to the database
     * Expect the return contains success information
     */
    @Test
    void addAdminRoleToUser() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder()
                .setRole(UserRole.STUDENT).setUserId(1).build();

        UserModel mockUserModel = Mockito.mock(UserModel.class);
        Roles theRole = new Roles();
        theRole.setRoleName("COURSE ADMINISTRATOR");
        theRole.setId(0);
        when(rolesRepository.findByRoleName("COURSE ADMINISTRATOR")).thenReturn(theRole);
        when(userModelService.getUserById(1)).thenReturn(mockUserModel);
        when(userModelService.saveEditedUser(any(UserModel.class))).thenReturn(true);
        doNothing().when(mockUserModel).addRoles(any(Roles.class));

        ArgumentCaptor<Roles> roleAdded = ArgumentCaptor.forClass(Roles.class);
        ArgumentCaptor<UserModel> addedUserModel = ArgumentCaptor.forClass(UserModel.class);

        UserRoleChangeResponse reply = userAccountServerService.addRoleToUserHelper(request);
        Assertions.assertEquals(true,reply.getIsSuccess());
        Mockito.verify(mockUserModel).addRoles(roleAdded.capture());
        Mockito.verify(userModelService).saveEditedUser(addedUserModel.capture());
    }

    /***
     * Test the method to delete role to user
     * Given te deleted role is Student
     * Verify that the role is deleted from the database
     * Expect the return contains success information
     */
    @Test
    void removeStudentRoleFromUser() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder()
            .setRole(UserRole.STUDENT).setUserId(1).build();

        UserModel mockUserModel = Mockito.mock(UserModel.class);
        Roles theRole = new Roles();
        theRole.setRoleName("STUDENT");
        theRole.setId(0);
        when(rolesRepository.findByRoleName("STUDENT")).thenReturn(theRole);
        when(userModelService.getUserById(1)).thenReturn(mockUserModel);
        when(userModelService.saveEditedUser(any(UserModel.class))).thenReturn(true);
        doNothing().when(mockUserModel).deleteRole(any(Roles.class));

        ArgumentCaptor<Roles> deletedRole = ArgumentCaptor.forClass(Roles.class);
        ArgumentCaptor<UserModel> deletedRoleModel = ArgumentCaptor.forClass(UserModel.class);

        UserRoleChangeResponse reply = userAccountServerService.removeRoleFromUserHelper(request);
        Assertions.assertEquals(true,reply.getIsSuccess());
        Mockito.verify(mockUserModel).deleteRole(deletedRole.capture());
        Mockito.verify(userModelService).saveEditedUser(deletedRoleModel.capture());

    }

    /***
     * Test the method to delete role to user
     * Given te deleted role is Teacher
     * Verify that the role is deleted from the database
     * Expect the return contains success information
     */
    @Test
    void removeTeacherRoleFromUser() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder()
                .setRole(UserRole.STUDENT).setUserId(1).build();

        UserModel mockUserModel = Mockito.mock(UserModel.class);
        Roles theRole = new Roles();
        theRole.setRoleName("TEACHER");
        theRole.setId(0);
        when(rolesRepository.findByRoleName("TEACHER")).thenReturn(theRole);
        when(userModelService.getUserById(1)).thenReturn(mockUserModel);
        when(userModelService.saveEditedUser(any(UserModel.class))).thenReturn(true);
        doNothing().when(mockUserModel).deleteRole(any(Roles.class));

        ArgumentCaptor<Roles> deletedRole = ArgumentCaptor.forClass(Roles.class);
        ArgumentCaptor<UserModel> deletedRoleModel = ArgumentCaptor.forClass(UserModel.class);

        UserRoleChangeResponse reply = userAccountServerService.removeRoleFromUserHelper(request);
        Assertions.assertEquals(true,reply.getIsSuccess());
        Mockito.verify(mockUserModel).deleteRole(deletedRole.capture());
        Mockito.verify(userModelService).saveEditedUser(deletedRoleModel.capture());

    }

    /***
     * Test the method to delete role to user
     * Given te deleted role is Admin
     * Verify that the role is deleted from the database
     * Expect the return contains success information
     */
    @Test
    void removeAdminRoleFromUser() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder()
                .setRole(UserRole.STUDENT).setUserId(1).build();

        UserModel mockUserModel = Mockito.mock(UserModel.class);
        Roles theRole = new Roles();
        theRole.setRoleName("COURSE ADMINISTRATOR");
        theRole.setId(0);
        when(rolesRepository.findByRoleName("COURSE ADMINISTRATOR")).thenReturn(theRole);
        when(userModelService.getUserById(1)).thenReturn(mockUserModel);
        when(userModelService.saveEditedUser(any(UserModel.class))).thenReturn(true);
        doNothing().when(mockUserModel).deleteRole(any(Roles.class));

        ArgumentCaptor<Roles> deletedRole = ArgumentCaptor.forClass(Roles.class);
        ArgumentCaptor<UserModel> deletedRoleModel = ArgumentCaptor.forClass(UserModel.class);

        UserRoleChangeResponse reply = userAccountServerService.removeRoleFromUserHelper(request);
        Assertions.assertEquals(true,reply.getIsSuccess());
        Mockito.verify(mockUserModel).deleteRole(deletedRole.capture());
        Mockito.verify(userModelService).saveEditedUser(deletedRoleModel.capture());

    }
}