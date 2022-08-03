package nz.ac.canterbury.seng302.identityprovider.server;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.server.GroupModelServerService;
import nz.ac.canterbury.seng302.identityprovider.service.GroupModelService;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Sets up tests for the group model server service used to delete object from the repository when given a request through GRPC.
 * This is done through mocking and stubbing.
 */
class GroupModelServerServiceTest {

    @Mock
    private GroupModelService groupModelService;

    @Mock
    private StreamObserver<DeleteGroupResponse> deleteObserver;

    @Mock
    private  StreamObserver<CreateGroupResponse> createObserver;

    @InjectMocks
    private GroupModelServerService groupModelServerService = Mockito.spy(GroupModelServerService.class);

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this); // This is required for Mockito annotations to work
    }

    /**
     * Tests the ability given a valid group ID is sent in the request through GRPC from the portfolio.
     * That it will successfully delete the group from the repository.
     */
    @Test
    void testDeleteExistingGroup() {
        // Build the request.
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder().setGroupId(1).build();

        // Setups up mock outcomes.
        when(groupModelService.removeGroup(anyInt())).thenReturn(true);

        // Runs tasks for deleting existing group.
        groupModelServerService.deleteGroup(request, deleteObserver);

        // Checks it ran .onCompleted().
        verify(deleteObserver, times(1)).onCompleted();
        // Sets up captures to get the response.
        ArgumentCaptor<DeleteGroupResponse> captor = ArgumentCaptor.forClass(DeleteGroupResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(deleteObserver, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        DeleteGroupResponse response = captor.getValue();

        // Checks that the group was removed.
        assertTrue(response.getIsSuccess());
    }

    /**
     * Tests creating a group with valid attributes is successful.
     */
    @Test
    void testCreatingValidGroup() {
        // Static variables
        String shortName = "Short Name";
        String longName = "Long Name";
        GroupModel testGroup = new GroupModel(shortName, longName, 1);

        // Build the request
        CreateGroupRequest request = CreateGroupRequest.newBuilder().setShortName(shortName).setLongName(longName).build();

        // Setups up mock outcomes.
        when(groupModelService.checkShortNameIsUnique(shortName)).thenReturn(true);
        when(groupModelService.checkLongNameIsUnique(longName)).thenReturn(true);
        when(groupModelService.addGroup(shortName, longName, 1)).thenReturn(testGroup);

        // Run creating of group
        groupModelServerService.createGroup(request, createObserver);

        // Checks it ran .onCompleted().
        verify(createObserver, times(1)).onCompleted();

        // Sets up captures to get the response.
        ArgumentCaptor<CreateGroupResponse> captor = ArgumentCaptor.forClass(CreateGroupResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(createObserver, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        CreateGroupResponse response = captor.getValue();

        //Checks a valid group is created
        assertTrue(response.getIsSuccess());
    }

    /**
     * Tests creating a group with invalid attributes is unsuccessful.
     */
    @Test
    void testCreatingInvalidGroup() {
        // Static variables
        String shortName = "Short Name";
        String longName = "Long Name";
        GroupModel testGroup = new GroupModel(shortName, longName, 1);

        // Build the request
        CreateGroupRequest request = CreateGroupRequest.newBuilder().setShortName(shortName).setLongName(longName).build();

        // Setups up mock outcomes.
        when(groupModelService.checkShortNameIsUnique(shortName)).thenReturn(false);
        when(groupModelService.checkLongNameIsUnique(longName)).thenReturn(false);

        // Run creating of group
        groupModelServerService.createGroup(request, createObserver);

        // Checks it ran .onCompleted().
        verify(createObserver, times(1)).onCompleted();

        // Sets up captures to get the response.
        ArgumentCaptor<CreateGroupResponse> captor = ArgumentCaptor.forClass(CreateGroupResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(createObserver, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        CreateGroupResponse response = captor.getValue();

        //Checks group is not created and correct error messages are produced
        assertFalse(response.getIsSuccess());
        assertEquals("Short Name not unique", response.getValidationErrors(0).getErrorText());
        assertEquals("Long Name not unique", response.getValidationErrors(1).getErrorText());

    }

}