package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Sets up tests for the group model server service used to delete object from the repository when given a request through GRPC.
 * This is done through mocking and stubbing.
 */
@ExtendWith(MockitoExtension.class)
class GroupModelServerServiceTest {

    @Mock
    private GroupModelService groupModelService;

    @Mock
    private StreamObserver<DeleteGroupResponse> deleteObserver;

    @Mock
    private StreamObserver<ModifyGroupDetailsResponse> modifyObserver;

    @InjectMocks
    private GroupModelServerService groupModelServerService = Mockito.spy(GroupModelServerService.class);

    private final GroupModel testGroup = new GroupModel("Short Name", "Long Name", 1);

    /**
     * Tests the ability given a valid group ID is sent in the request through GRPC from the portfolio,
     * that it will successfully delete the group from the repository.
     */
    @Test
    void testDeleteExistingGroup() {
        // Build the request.
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder().setGroupId(1).build();

        // Setup mock outcomes.
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
     * Tests the ability given an invalid group ID is sent in the request through GRPC from the portfolio,
     * that it will unsuccessfully delete the group from the repository.
     */
    @Test
    void testDeleteNonExistingGroup() {
        // Build the request.
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder().setGroupId(1).build();

        // Setup mock outcomes.
        when(groupModelService.removeGroup(anyInt())).thenReturn(false);

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
        assertFalse(response.getIsSuccess());
    }

    /**
     * Tests the modifyGroupDetails method when given a valid editing scenario (names don't conflict with other groups).
     */
    @Test
    void testModifyGroupDetailsValid() {
        // Build the request.
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName())
                .build();

        // Setup mock outcomes.
        when(groupModelService.checkShortNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getShortName())).thenReturn(true);
        when(groupModelService.checkLongNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getLongName())).thenReturn(true);
        when(groupModelService.editGroup(testGroup.getGroupId(), testGroup.getShortName(), testGroup.getLongName())).thenReturn(true);

        // Runs tasks for modifying existing group.
        groupModelServerService.modifyGroupDetails(request, modifyObserver);

        // Checks it ran .onCompleted().
        verify(modifyObserver, times(1)).onCompleted();
        // Sets up captures to get the response.
        ArgumentCaptor<ModifyGroupDetailsResponse> captor = ArgumentCaptor.forClass(ModifyGroupDetailsResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(modifyObserver, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        ModifyGroupDetailsResponse response = captor.getValue();

        // Checks response attributes.
        assertTrue(response.getIsSuccess());
        assertEquals("Group saved", response.getMessage());
        assertEquals(0, response.getValidationErrorsCount());
    }

    /**
     * Tests the modifyGroupDetails method when the short name is not unique.
     */
    @Test
    void testModifyGroupDetailsShortNameNotUnique() {
        // Build the request.
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName())
                .build();

        // Setup mock outcomes.
        when(groupModelService.checkShortNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getShortName())).thenReturn(false);
        when(groupModelService.checkLongNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getLongName())).thenReturn(true);

        // Runs tasks for modifying existing group.
        groupModelServerService.modifyGroupDetails(request, modifyObserver);

        // Checks it ran .onCompleted().
        verify(modifyObserver, times(1)).onCompleted();
        // Sets up captures to get the response.
        ArgumentCaptor<ModifyGroupDetailsResponse> captor = ArgumentCaptor.forClass(ModifyGroupDetailsResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(modifyObserver, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        ModifyGroupDetailsResponse response = captor.getValue();

        // Checks response attributes.
        assertFalse(response.getIsSuccess());
        assertEquals("Name was not unique", response.getMessage());
        assertEquals(1, response.getValidationErrorsCount());

        ValidationError validationError = response.getValidationErrors(0);
        assertEquals("shortName", validationError.getFieldName());
        assertEquals("Short Name not unique", validationError.getErrorText());
    }

    /**
     * Tests the modifyGroupDetails method when the long name is not unique.
     */
    @Test
    void testModifyGroupDetailsLongNameNotUnique() {
        // Build the request.
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName())
                .build();

        // Setup mock outcomes.
        when(groupModelService.checkShortNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getShortName())).thenReturn(true);
        when(groupModelService.checkLongNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getLongName())).thenReturn(false);

        // Runs tasks for modifying existing group.
        groupModelServerService.modifyGroupDetails(request, modifyObserver);

        // Checks it ran .onCompleted().
        verify(modifyObserver, times(1)).onCompleted();
        // Sets up captures to get the response.
        ArgumentCaptor<ModifyGroupDetailsResponse> captor = ArgumentCaptor.forClass(ModifyGroupDetailsResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(modifyObserver, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        ModifyGroupDetailsResponse response = captor.getValue();

        // Checks response attributes.
        assertFalse(response.getIsSuccess());
        assertEquals("Name was not unique", response.getMessage());
        assertEquals(1, response.getValidationErrorsCount());

        ValidationError validationError = response.getValidationErrors(0);
        assertEquals("longName", validationError.getFieldName());
        assertEquals("Long Name not unique", validationError.getErrorText());
    }

    /**
     * Tests the modifyGroupDetails method when the long name is not unique.
     */
    @Test
    void testModifyGroupDetailsBothNamesNotUnique() {
        // Build the request.
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName())
                .build();

        // Setup mock outcomes.
        when(groupModelService.checkShortNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getShortName())).thenReturn(false);
        when(groupModelService.checkLongNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getLongName())).thenReturn(false);

        // Runs tasks for modifying existing group.
        groupModelServerService.modifyGroupDetails(request, modifyObserver);

        // Checks it ran .onCompleted().
        verify(modifyObserver, times(1)).onCompleted();
        // Sets up captures to get the response.
        ArgumentCaptor<ModifyGroupDetailsResponse> captor = ArgumentCaptor.forClass(ModifyGroupDetailsResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(modifyObserver, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        ModifyGroupDetailsResponse response = captor.getValue();

        // Checks response attributes.
        assertFalse(response.getIsSuccess());
        assertEquals("Name was not unique", response.getMessage());
        assertEquals(2, response.getValidationErrorsCount());

        ValidationError validationErrorShort = response.getValidationErrors(0);
        assertEquals("shortName", validationErrorShort.getFieldName());
        assertEquals("Short Name not unique", validationErrorShort.getErrorText());

        ValidationError validationErrorLong = response.getValidationErrors(1);
        assertEquals("longName", validationErrorLong.getFieldName());
        assertEquals("Long Name not unique", validationErrorLong.getErrorText());
    }

    /**
     * Tests the modifyGroupDetails method when the group cannot be found with the given id.
     */
    @Test
    void testModifyGroupDetailsGroupNotFound() {
        // Build the request.
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName())
                .build();

        // Setup mock outcomes.
        when(groupModelService.checkShortNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getShortName())).thenReturn(true);
        when(groupModelService.checkLongNameIsUniqueEditing(testGroup.getGroupId(), testGroup.getLongName())).thenReturn(true);
        when(groupModelService.editGroup(testGroup.getGroupId(), testGroup.getShortName(), testGroup.getLongName())).thenReturn(false);

        // Runs tasks for modifying existing group.
        groupModelServerService.modifyGroupDetails(request, modifyObserver);

        // Checks it ran .onCompleted().
        verify(modifyObserver, times(1)).onCompleted();
        // Sets up captures to get the response.
        ArgumentCaptor<ModifyGroupDetailsResponse> captor = ArgumentCaptor.forClass(ModifyGroupDetailsResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(modifyObserver, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        ModifyGroupDetailsResponse response = captor.getValue();

        // Checks response attributes.
        assertFalse(response.getIsSuccess());
        assertEquals("Group not found", response.getMessage());
        assertEquals(0, response.getValidationErrorsCount());
    }
}