package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
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
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private ElementService elementService; // needed to load application context

    @MockBean
    private UserAccountClientService userAccountClientService; // needed to load application context

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(GroupController.class).build();
    }

    /**
     * Test that if given any valid GroupId, table refresh successfully.
     * @throws Exception Can be caused during mocking the MVC system.
     */
    @Test
    void testRefreshGroupTable() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);
        int GroupId = 1;

        mockMvc.perform(get("/groups/local?groupId=" + GroupId))
                .andExpect(status().isOk());
    }

    /**
     * Test that when a DELETE call is made to delete a group of a given valid id, that the controller returns a successful value.
     * @throws Exception    Can be caused during mocking the MVC system.
     */
    @Test
    void testDeleteExistingGroup() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        int expectedGroupId = 1;
        when(groupService.deleteGroup(expectedGroupId)).thenReturn(DeleteGroupResponse.newBuilder().setIsSuccess(true).build());

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
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        int expectedGroupId = 1;
        when(groupService.deleteGroup(expectedGroupId)).thenReturn(DeleteGroupResponse.newBuilder().setIsSuccess(false).build());

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
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        int expectedGroupId = 1;
        Group group = new Group();
        group.setShortName("seng-302");
        group.setLongName("A group of seng students working on a project.");
        group.setGroupId(expectedGroupId);


        when(groupService.editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName()))
                .thenReturn(ModifyGroupDetailsResponse.newBuilder().setIsSuccess(true).build());

        mockMvc.perform(post("/edit-group/" + expectedGroupId).flashAttr("group", group))
                .andExpect(status().isOk())
                .andExpect(model().attribute("group", group))
                .andExpect(view().name("group::groupCard"));

        verify(groupService, times(1)).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());
    }

    /**
     * Test that when a POST call is made to edit a group of a given invalid id, that the controller returns an un-successful value.
     * @throws Exception    Can be caused during mocking the MVC system.
     */
    @Test
    void testEditNonExistingGroup() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        int expectedGroupId = 1;
        Group group = new Group();
        group.setShortName("seng-302");
        group.setLongName("A group of seng students working on a project.");
        group.setGroupId(expectedGroupId);


        when(groupService.editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName()))
                .thenReturn(ModifyGroupDetailsResponse.newBuilder().setIsSuccess(false).build());

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
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        int expectedGroupId = 1;
        Group group = new Group();
        group.setShortName("Not a short name!"); // 17 CHARACTERS
        group.setLongName("A group of seng students working on a project.");
        group.setGroupId(expectedGroupId);

        ValidationError error = ValidationError.newBuilder().setErrorText("Short name is to long.").build();
        ModifyGroupDetailsResponse response = ModifyGroupDetailsResponse.newBuilder().addValidationErrors(0, error).setIsSuccess(false).build();

        when(groupService.editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName())).thenReturn(response);

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
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        int expectedGroupId = 1;
        Group group = new Group();
        group.setShortName("seng-302");
        group.setLongName("Not a long name! Not a long name! Not a long name! Not a long name!"); // 67 CHARACTERS
        group.setGroupId(expectedGroupId);

        ValidationError error = ValidationError.newBuilder().setErrorText("Long name is to long.").build();
        ModifyGroupDetailsResponse response = ModifyGroupDetailsResponse.newBuilder().addValidationErrors(0, error).setIsSuccess(false).build();

        when(groupService.editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName())).thenReturn(response);

        mockMvc.perform(post("/edit-group/" + expectedGroupId).flashAttr("group", group))
                .andExpect(status().is4xxClientError())
                .andExpect(model().attribute("groupLongNameAlertMessage", "Long name is to long."))
                .andExpect(model().attributeDoesNotExist("groupShortNameAlertMessage"))
                .andExpect(view().name("fragments/groupModal::groupModalBody"));

        verify(groupService, times(1)).editGroupDetails(expectedGroupId, group.getShortName(), group.getLongName());
    }
}