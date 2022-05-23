package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import io.grpc.stub.StreamObservers;
import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.RolesRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyRoleOfUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

    @Test
    void removeRoleFromUser() {
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
}