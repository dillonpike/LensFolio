package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the GroupSettingsController class. Tests application endpoints.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GroupSettingsController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroupSettingsControllerTest {

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

    @SpyBean
    private GroupService groupService;

    @MockBean
    private ElementService elementService; // needed to load application context

    @MockBean
    private UserAccountClientService userAccountClientService; // needed to load application context

    @MockBean
    private RegisterClientService registerClientService; // needed to load application context

    @MockBean
    private GitLabApiService gitLabApiService; // needed to load application context

    private final Group testGroup = new Group("Test", "Test Group", 1);

    private final Group membersGroup = new Group("Short Name", "Long Name", 2);

    private final Group teachersGroup = new Group("Short Name", "Long Name", 1);

    /**
     * Build the mockMvc object.
     */
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(GroupSettingsController.class).build();
    }

    /**
     * Mock security contexts and set group ids.
     * Group ids only need to be set once, but weren't being set properly in setUp.
     */
    @BeforeEach
    public void setUpBeforeEach() {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);
        testGroup.setGroupId(5);
        membersGroup.setGroupId(1);
        teachersGroup.setGroupId(2);
    }


    /**
     * Test that the group settings page is loaded with the correct short and long name when given a valid id.
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void groupSettingsValid() throws Exception {
        doReturn(GroupDetailsResponse.newBuilder()
                .setGroupId(testGroup.getGroupId()).setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName()).build())
                .when(groupService).getGroupDetails(testGroup.getGroupId());

        mockMvc.perform(get("/groupSettings").param("groupId", Integer.toString(testGroup.getGroupId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("groupShortName", testGroup.getShortName()))
                .andExpect(model().attribute("groupLongName", testGroup.getLongName()));
    }

    /**
     * Test that the group settings page redirects to the groups page when trying to access the group settings page for
     * the special non-members group.
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void groupSettingsMembersGroup() throws Exception {
        doReturn(GroupDetailsResponse.newBuilder()
                .setGroupId(membersGroup.getGroupId()).setShortName(membersGroup.getShortName())
                .setLongName(membersGroup.getLongName()).build())
                .when(groupService).getGroupDetails(membersGroup.getGroupId());

        mockMvc.perform(get("/groupSettings").param("groupId", Integer.toString(membersGroup.getGroupId())))
                .andExpect(redirectedUrl("/groups"));
    }

    /**
     * Test that the group settings page redirects to the groups page when trying to access the group settings page for
     * the speacial teachers group.
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void groupSettingsTeachersGroup() throws Exception {
        doReturn(GroupDetailsResponse.newBuilder()
                .setGroupId(teachersGroup.getGroupId()).setShortName(teachersGroup.getShortName())
                .setLongName(teachersGroup.getLongName()).build())
                .when(groupService).getGroupDetails(teachersGroup.getGroupId());

        mockMvc.perform(get("/groupSettings").param("groupId", Integer.toString(teachersGroup.getGroupId())))
                .andExpect(redirectedUrl("/groups"));
    }

    /**
     * Test that the group settings page redirects to the groups page when trying to access the group settings page for
     * a group that doesn't exist
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void groupSettingsInvalidGroup() throws Exception {
        int invalidId = 18432;
        int responseId = 0;
        doReturn(GroupDetailsResponse.newBuilder()
                .setGroupId(responseId).build())
                .when(groupService).getGroupDetails(invalidId);

        mockMvc.perform(get("/groupSettings").param("groupId", Integer.toString(invalidId)))
                .andExpect(redirectedUrl("/groups"));
    }
}