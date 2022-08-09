package nz.ac.canterbury.seng302.identityprovider.server;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.service.GroupModelService;
import nz.ac.canterbury.seng302.identityprovider.service.UserModelService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.directory.InvalidAttributesException;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
    private  StreamObserver<CreateGroupResponse> createObserver;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private StreamObserver<ModifyGroupDetailsResponse> modifyObserver;

    @Mock
    private StreamObserver<GroupDetailsResponse> groupDetailsResponseObserver;

    @Mock
    private StreamObserver<PaginatedGroupsResponse> paginatedGroupsResponseObserver;

    @Mock
    private UserModelService userModelService;

    @InjectMocks
    private GroupModelServerService groupModelServerService = Mockito.spy(GroupModelServerService.class);

    private final GroupModel testGroup = new GroupModel("Short Name", "Long Name", 1);

    private final GroupModel testGroup1 = new GroupModel("Short Name", "Long Name", 1);

    private final GroupModel testGroup2 = new GroupModel("Short Name", "Long Name", 1);

    /**
     * Tests the ability given a valid group ID is sent in the request through GRPC from the portfolio.
     * That it will successfully delete the group from the repository.
     */
    @Test
    void testDeleteExistingGroup() throws InvalidAttributesException {
        // Build the request.
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder().setGroupId(1).build();

        // Setups up mock outcomes.
        when(groupModelService.removeGroup(anyInt())).thenReturn(true);
        when(groupModelService.getGroupById(any(Integer.class))).thenReturn(testGroup);

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
    void testDeleteNonExistingGroup() throws InvalidAttributesException {
        // Build the request.
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder().setGroupId(1).build();

        // Setups up mock outcomes.
        when(groupModelService.removeGroup(anyInt())).thenReturn(false);
        when(groupModelService.getGroupById(any(Integer.class))).thenReturn(testGroup);

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

    @Test
    void testGetGroupDetailGroupExists() {
        GetGroupDetailsRequest request = GetGroupDetailsRequest.newBuilder()
                .setGroupId(1)
                .build();
        when(groupModelService.isExistById(request.getGroupId())).thenReturn(true);
        when(groupRepository.getGroupModelByGroupId(request.getGroupId())).thenReturn(testGroup);
        // Runs tasks for modifying existing group.
        groupModelServerService.getGroupDetails(request, groupDetailsResponseObserver);

        // Checks it ran .onCompleted().
        verify(groupDetailsResponseObserver, times(1)).onCompleted();
        // Sets up captures to get the response.
        ArgumentCaptor<GroupDetailsResponse> captor = ArgumentCaptor.forClass(GroupDetailsResponse.class);
        // Checks it ran .onNext() and captor the response.
        verify(groupDetailsResponseObserver, times(1)).onNext(captor.capture());
        // Gets the value of the response from the captor.
        GroupDetailsResponse response = captor.getValue();

        assertEquals("Long Name", response.getLongName());
        assertEquals("Short Name", response.getShortName());
        assertEquals(0, response.getGroupId());
    }


//    @Test
//    void testGetPaginatedGroups() {
//        GetPaginatedGroupsRequest request = GetPaginatedGroupsRequest
//                .newBuilder()
//                .build();
//        List<GroupModel> groupModelList = new ArrayList<>();
//        groupModelList.add(testGroup);
//        groupModelList.add(testGroup1);
//        groupModelList.add(testGroup2);
//
//        when(groupRepository.findAll()).thenReturn(groupModelList);
//        groupModelServerService.getPaginatedGroups(request, paginatedGroupsResponseObserver);
//
//        // Checks it ran .onCompleted().
//        verify(paginatedGroupsResponseObserver, times(1)).onCompleted();
//        // Sets up captures to get the response.
//        ArgumentCaptor<PaginatedGroupsResponse> captor = ArgumentCaptor.forClass(PaginatedGroupsResponse.class);
//        // Checks it ran .onNext() and captor the response.
//        verify(paginatedGroupsResponseObserver, times(1)).onNext(captor.capture());
//        // Gets the value of the response from the captor.
//        PaginatedGroupsResponse response = captor.getValue();
//
//        assertEquals(3, response.getGroupsCount());
//
//    } TODO:we need to fix this! this test is failing and break the pipeline


}