package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.Roles;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.repository.RolesRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class GroupModelServerServiceTest {

    @Mock
    private GroupModelService groupModelService;

    @InjectMocks
    private GroupModelServerService groupModelServerService = Mockito.spy(GroupModelServerService.class);

    @Test
    void testDeleteExistingGroup() {
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder().setGroupId(1).build();

        when(groupModelService.removeGroup(anyInt())).thenReturn(true);


        UserRoleChangeResponse reply = groupModelServerService.deleteGroup(request);
        Assertions.assertEquals(true,reply.getIsSuccess());
        Mockito.verify(mockUserModel).deleteRole(deletedRole.capture());
        Mockito.verify(userModelService).saveEditedUser(deletedRoleModel.capture());

    }

}