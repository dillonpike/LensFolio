package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
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
    private StreamObserver<DeleteGroupResponse> observer;

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
        groupModelServerService.deleteGroup(request, observer);

        // Checks it ran .onCompleted().
        verify(observer, times(1)).onCompleted();
        // Sets up captures to get the response.
        ArgumentCaptor<DeleteGroupResponse> captor = ArgumentCaptor.forClass(DeleteGroupResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(observer, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        DeleteGroupResponse response = captor.getValue();

        // Checks that the group was removed.
        assertTrue(response.getIsSuccess());
    }

}