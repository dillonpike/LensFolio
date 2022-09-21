package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests the group controllers post and get methods to ensure consistent responses.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

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
            .setId(1)
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

    @Autowired
    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(GroupController.class).build();

    @SpyBean
    private GroupService groupService;

    @MockBean
    private ElementService elementService; // needed to load application context

    @MockBean
    private UserAccountClientService userAccountClientService; // needed to load application context

    @MockBean
    private RegisterClientService registerClientService; // needed to load application context

    @MockBean
    private PhotoService photoService;

    @MockBean
    private PermissionService permissionService;

    private final Group testGroup = new Group("Test", "Test Group", 1);

    /**
     * Set up the mock security context
     */
    @BeforeEach
    void setup() {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(1);
        when(registerClientService.getUserData(1)).thenReturn(mockUser);
    }

    /**
     * Test GET request for group page at initial stage.
     * @throws Exception Can be caused during mocking the MVC system.
     */
    @Test
    void testShowGroupPage() throws Exception {

        GroupDetailsResponse groupDetailsResponse = GroupDetailsResponse.newBuilder().setGroupId(testGroup.getGroupId())
                .setShortName(testGroup.getShortName()).setLongName(testGroup.getLongName()).build();

        PaginatedGroupsResponse paginatedGroupsResponse = PaginatedGroupsResponse.newBuilder().addGroups(groupDetailsResponse).build();

        doReturn(paginatedGroupsResponse).when(groupService).getPaginatedGroups(1,1, "null", false);

        mockMvc.perform(get("/groups")).andExpect(status().isOk())
                .andExpect(model().attribute("groupList", paginatedGroupsResponse.getGroupsList()));

    }


    /**
     * Test that when a DELETE call is made to delete a group of a given valid id, that the controller returns a successful value.
     * @throws Exception    Can be caused during mocking the MVC system.
     */
    @Test
    void testDeleteExistingGroup() throws Exception {

        int expectedGroupId = 1;
        doReturn(DeleteGroupResponse.newBuilder().setIsSuccess(true).build())
                .when(groupService).deleteGroup(expectedGroupId);

        mockMvc.perform(delete("/delete-group/" + expectedGroupId))
                .andExpect(status().is2xxSuccessful());

        verify(groupService, times(1)).deleteGroup(expectedGroupId);
    }

    /**
     * Test that when a DELETE call is made to delete a group of a given invalid id, that the controller returns an un-successful value.
     * @throws Exception    Can be caused during mocking the MVC system.
     */
    @Test
    void testDeleteNonExistingGroup() throws Exception {

        int expectedGroupId = 1;
        doReturn(DeleteGroupResponse.newBuilder().setIsSuccess(false).build())
                .when(groupService).deleteGroup(expectedGroupId);

        mockMvc.perform(delete("/delete-group/" + expectedGroupId))
                .andExpect(status().is4xxClientError());

        verify(groupService, times(1)).deleteGroup(expectedGroupId);
    }

    /**
     * Test that when a POST call is made to edit a group of a given valid id, that the controller returns a successful value.
     * @throws Exception    Can be caused during mocking the MVC system.
     */
    @Test
    void testEditExistingGroup() throws Exception {

        int expectedGroupId = 1;
        Group group = new Group();
        group.setShortName("seng-302");
        group.setLongName("A group of seng students working on a project.");
        group.setGroupId(expectedGroupId);

        doReturn(ModifyGroupDetailsResponse.newBuilder().setIsSuccess(true).build())
                .when(groupService).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());

        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setGroupId(group.getGroupId())
                .setShortName(group.getShortName()).setLongName(group.getLongName()).build();

        doReturn(response).when(groupService).getGroupDetails(group.getGroupId());

        mockMvc.perform(post("/edit-group/" + expectedGroupId).flashAttr("group", group))
                .andExpect(status().isOk())
                .andExpect(model().attribute("group", response))
                .andExpect(view().name("group::groupCard"));

        verify(groupService, times(1)).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());
    }

    /**
     * Test that when a POST call is made to edit a group of a given invalid id, that the controller returns an un-successful value.
     * @throws Exception    Can be caused during mocking the MVC system.
     */
    @Test
    void testEditNonExistingGroup() throws Exception {

        int expectedGroupId = 1;
        Group group = new Group();
        group.setShortName("seng-302");
        group.setLongName("A group of seng students working on a project.");
        group.setGroupId(expectedGroupId);

        doReturn(ModifyGroupDetailsResponse.newBuilder().setIsSuccess(false).build())
                .when(groupService).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());

        mockMvc.perform(post("/edit-group/" + expectedGroupId).flashAttr("group", group))
                .andExpect(status().is4xxClientError())
                .andExpect(model().attributeDoesNotExist("groupShortNameAlertMessage"))
                .andExpect(model().attributeDoesNotExist("groupShortNameAlertMessage"))
                .andExpect(view().name("fragments/groupModal::groupModalBody"));

        verify(groupService, times(1)).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());
    }

    /**
     * Test that when a POST call is made to edit a group of a given invalid id, that the controller returns an un-successful value.
     * @throws Exception    Can be caused during mocking the MVC system.
     */
    @Test
    void testEditExistingGroupWithInvalidShortName() throws Exception {

        int expectedGroupId = 1;
        Group group = new Group();
        group.setShortName("Not a short name!"); // 17 CHARACTERS
        group.setLongName("A group of seng students working on a project.");
        group.setGroupId(expectedGroupId);

        ValidationError error = ValidationError.newBuilder().setErrorText("Short name is to long.").build();
        ModifyGroupDetailsResponse response = ModifyGroupDetailsResponse.newBuilder().addValidationErrors(0, error).setIsSuccess(false).build();

        doReturn(response).when(groupService).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());

        mockMvc.perform(post("/edit-group/" + expectedGroupId).flashAttr("group", group))
                .andExpect(status().is4xxClientError())
                .andExpect(model().attribute("groupShortNameAlertMessage", "Short name is to long."))
                .andExpect(model().attributeDoesNotExist("groupLongNameAlertMessage"))
                .andExpect(view().name("fragments/groupModal::groupModalBody"));

        verify(groupService, times(1)).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());
    }

    /**
     * Test that when a POST call is made to edit a group of a given invalid id, that the controller returns an un-successful value.
     * @throws Exception    Can be caused during mocking the MVC system.
     */
    @Test
    void testEditExistingGroupWithInvalidLongName() throws Exception {

        int expectedGroupId = 1;
        Group group = new Group();
        group.setShortName("seng-302");
        group.setLongName("Not a long name! Not a long name! Not a long name! Not a long name!"); // 67 CHARACTERS
        group.setGroupId(expectedGroupId);

        ValidationError error = ValidationError.newBuilder().setErrorText("Long name is to long.").build();
        ModifyGroupDetailsResponse response = ModifyGroupDetailsResponse.newBuilder().addValidationErrors(0, error).setIsSuccess(false).build();

        doReturn(response).when(groupService).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());

        mockMvc.perform(post("/edit-group/" + expectedGroupId).flashAttr("group", group))
                .andExpect(status().is4xxClientError())
                .andExpect(model().attribute("groupLongNameAlertMessage", "Long name is to long."))
                .andExpect(model().attributeDoesNotExist("groupShortNameAlertMessage"))
                .andExpect(view().name("fragments/groupModal::groupModalBody"));

        verify(groupService, times(1)).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());
    }

    /**
     * Tests removing a user from a group POST controller endpoint. The user is removed successfully.
     * @throws Exception Can be caused during mocking the MVC system.
     */
    @Test
    void testRemovingUserFromGroup() throws Exception {

        int groupId = 5;
        Group group = new Group();
        group.setShortName("New-group");
        group.setLongName("My mock group");
        group.setGroupId(groupId);

        group.addMember(mockUser.getId());

        List<Integer> groupMembers = Collections.singletonList(mockUser.getId());

        when(elementService.getUserHighestRole(mockUser)).thenReturn("admin");
        RemoveGroupMembersResponse response = RemoveGroupMembersResponse.newBuilder().setIsSuccess(true).build();
        GroupDetailsResponse groupResponse = GroupDetailsResponse.newBuilder().setGroupId(groupId).addMembers(mockUser).build();

        doReturn(groupResponse).when(groupService).getGroupDetails(groupId);
        doReturn(response).when(groupService).removeMembersFromGroup(groupId, groupMembers);


        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("groupId", String.valueOf(groupId));
        parameters.add("userIds", String.valueOf(mockUser.getId()));

        mockMvc.perform(post("/remove-users").params(parameters))
                .andExpect(status().isOk())
                .andExpect(view().name("group::groupCard"));

        verify(groupService, times(1)).removeMembersFromGroup(groupId, groupMembers);
    }

    /**
     * Tests removing a user from a group POST controller endpoint. The user is not removed.
     * @throws Exception Can be caused during mocking the MVC system.
     */
    @Test
    void testRemovingUserFromGroupFail() throws Exception {

        int groupId = 5;
        Group group = new Group();
        group.setShortName("New-group");
        group.setLongName("My mock group");
        group.setGroupId(groupId);

        group.addMember(mockUser.getId());

        List<Integer> groupMembers = Collections.singletonList(mockUser.getId());

        when(elementService.getUserHighestRole(mockUser)).thenReturn("admin");
        RemoveGroupMembersResponse response = RemoveGroupMembersResponse.newBuilder().setIsSuccess(false).build();
        GroupDetailsResponse groupResponse = GroupDetailsResponse.newBuilder().setGroupId(groupId).addMembers(mockUser).build();

        doReturn(groupResponse).when(groupService).getGroupDetails(groupId);
        doReturn(response).when(groupService).removeMembersFromGroup(groupId, groupMembers);


        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("groupId", String.valueOf(groupId));
        parameters.add("userIds", String.valueOf(mockUser.getId()));

        mockMvc.perform(post("/remove-users").params(parameters))
                .andExpect(status().is4xxClientError());

        verify(groupService, times(1)).removeMembersFromGroup(groupId, groupMembers);
    }
}