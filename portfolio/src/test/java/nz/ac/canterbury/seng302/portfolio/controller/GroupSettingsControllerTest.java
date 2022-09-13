package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupSettingsRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.gitlab4j.api.models.Commit;
import org.hibernate.ObjectNotFoundException;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
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
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the GroupSettingsController class. Tests application endpoints related to group settings.
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
            .build();

    @Autowired
    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(GroupSettingsController.class).build();

    @SpyBean
    private GroupSettingsController groupSettingsController;

    @SpyBean
    private GroupService groupService;

    @SpyBean
    private GroupSettingsService groupSettingsServiceSpy;

    @MockBean
    private GroupSettingsService groupSettingsService;

    @Mock
    private GroupSettingsService mockGroupSettingsService;

    @MockBean
    private GroupSettingsRepository groupSettingsRepository;

    @MockBean
    private ElementService elementService; // needed to load application context

    @MockBean
    private UserAccountClientService userAccountClientService; // needed to load application context

    @MockBean
    private RegisterClientService registerClientService; // needed to load application context

    @MockBean
    private PermissionService permissionService; // needed to load application context

    @MockBean
    private GitLabApiService gitLabApiService; // needed to load application context


    private static final Group testGroup = new Group("Test", "Test Group", 1);

    private static final Group membersGroup = new Group("Short Name", "Long Name", 2);

    private static final Group teachersGroup = new Group("Short Name", "Long Name", 1);

    private static final List<Commit> testCommits = new ArrayList<>();

    private static final GroupSettings testGroupSettings = new GroupSettings(123, "This is test settings", "test123", 123, "https://eng-git.canterbury.ac.nz");
    /**
     * Build the mockMvc object and mock security contexts.
     */
    @BeforeEach
    public void setUpMocks() {

        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);
    }

    /**
     * Set group ids (did not work with @Before tag).
     */
    @BeforeAll
    static void setUpIds() {
        testGroup.setGroupId(5);
        membersGroup.setGroupId(1);
        teachersGroup.setGroupId(2);
        testGroupSettings.setGroupSettingsId(1);

        for (int i = 0; i < 5; i++) {
            Commit testCommit = new Commit();
            testCommit.setTitle(String.format("Test Commit %d", i));
            testCommits.add(testCommit);
        }
    }


    /**
     * Test that the group settings page is loaded with the correct short and long name when given a valid id.
     * Also test that the group settings page shows error banner if the repository that has been set up is not reachable.
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void groupSettingsValidAndWhenRepositoryIsUnreachable() throws Exception {
        doReturn(GroupDetailsResponse.newBuilder()
                .setGroupId(testGroup.getGroupId()).setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName()).build())
                .when(groupService).getGroupDetails(testGroup.getGroupId());

        when(registerClientService.getUserData(any(Integer.class))).thenReturn(mockUser);

        when(groupSettingsService.getGroupSettingsByGroupId(any(Integer.class))).thenReturn(testGroupSettings);
        when(gitLabApiService.checkGitLabToken(any(Integer.class), any(String.class), any(String.class))).thenReturn(false);

        mockMvc.perform(get("/groupSettings").param("groupId", Integer.toString(testGroup.getGroupId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("groupShortName", testGroup.getShortName()))
                .andExpect(model().attribute("groupLongName", testGroup.getLongName()))
                .andExpect(model().attribute("groupSettingsAlertMessage", "Repository Is Unreachable With The Current Settings"));
    }

    /**
     * Test that the group settings page is loaded with the correct short and long name when given a valid id
     * Also test if the user has write access to the current page if user is in current group.
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void groupSettingValidAndUserIsInGroupCheckWritePermission() throws Exception {
        doReturn(GroupDetailsResponse.newBuilder()
                .setGroupId(testGroup.getGroupId()).setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName()).build())
                .when(groupService).getGroupDetails(testGroup.getGroupId());

        when(registerClientService.getUserData(any(Integer.class))).thenReturn(mockUser);

        when(groupSettingsService.getGroupSettingsByGroupId(any(Integer.class))).thenReturn(testGroupSettings);
        when(gitLabApiService.checkGitLabToken(any(Integer.class), any(String.class), any(String.class))).thenReturn(true);
        when(permissionService.isValidToModifyGroupSettingPage(any(Integer.class), any(Integer.class))).thenReturn(true);
        mockMvc.perform(get("/groupSettings").param("groupId", Integer.toString(testGroup.getGroupId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("groupShortName", testGroup.getShortName()))
                .andExpect(model().attribute("groupLongName", testGroup.getLongName()))
                .andExpect(model().attribute("isValidToModify", true));
    }

    /**
     * Test that the group settings page is loaded with the correct short and long name when given a valid id
     * Also test if the user has write access to the current page if user is not in current group.
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void groupSettingValidAndUserIsNotInGroupCheckWritePermission() throws Exception {
        doReturn(GroupDetailsResponse.newBuilder()
                .setGroupId(testGroup.getGroupId()).setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName()).build())
                .when(groupService).getGroupDetails(testGroup.getGroupId());

        when(registerClientService.getUserData(any(Integer.class))).thenReturn(mockUser);

        when(groupSettingsService.getGroupSettingsByGroupId(any(Integer.class))).thenReturn(testGroupSettings);
        when(gitLabApiService.checkGitLabToken(any(Integer.class), any(String.class), any(String.class))).thenReturn(true);
        when(permissionService.isValidToModifyGroupSettingPage(any(Integer.class), any(Integer.class))).thenReturn(false);
        mockMvc.perform(get("/groupSettings").param("groupId", Integer.toString(testGroup.getGroupId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("groupShortName", testGroup.getShortName()))
                .andExpect(model().attribute("groupLongName", testGroup.getLongName()))
                .andExpect(model().attribute("isValidToModify", false));
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

        when(groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId())).thenReturn(testGroupSettings);


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

    /**
     * In this test, it is assumed that the groupId is valid but the group has no repository set up yet
     * Test that endpoint  will return a model indicating the group does not have repository
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void groupDoesNotHaveRepo() throws Exception {
        ObjectNotFoundException exception = new ObjectNotFoundException(testGroup.getGroupId(), "test");
        doReturn(GroupDetailsResponse.newBuilder()
                .setGroupId(testGroup.getGroupId()).setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName()).build())
                .when(groupService).getGroupDetails(testGroup.getGroupId());

        when(registerClientService.getUserData(any(Integer.class))).thenReturn(mockUser);

        when(gitLabApiService.getContributors(any(Integer.class))).thenThrow(exception);
        when(groupSettingsService.getGroupSettingsByGroupId(any(Integer.class))).thenReturn(testGroupSettings);
        when(gitLabApiService.checkGitLabToken(any(Integer.class), any(String.class), any(String.class))).thenReturn(false);
        mockMvc.perform(get("/groupSettings").param("groupId", Integer.toString(testGroup.getGroupId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("groupShortName", testGroup.getShortName()))
                .andExpect(model().attribute("groupLongName", testGroup.getLongName()))
                .andExpect(model().attribute("isRepoExist", false));
    }

    /**
     * Test that the endpoint return a model which contains commits, also check that when
     * selected Branch Name is All Branches and selected user is All Users, we called getCommits() function
     * from GitlabApi Service with value of null for branchName parameter and userEmail parameter
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void getCommitsInAllBranchesAndAllUsers() throws Exception {
        ArgumentCaptor<String> branchNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);

        when(gitLabApiService.getCommits(eq(testGroup.getGroupId()), any(),any())).thenReturn(testCommits);

        mockMvc.perform(get("/repository-commits").param("groupId", Integer.toString(testGroup.getGroupId())).param("branchName", "All Branches").param("userEmail", "All Users"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("commitList", testCommits));
        verify(gitLabApiService, times(1)).getCommits(eq(testGroup.getGroupId()),branchNameCaptor.capture(), userCaptor.capture());
        assertNull(branchNameCaptor.getValue());
        assertNull(userCaptor.getValue());

    }

    @Test
    void testGroupSettingUpdate() throws Exception {
        doReturn(GroupDetailsResponse.newBuilder()
                .setGroupId(testGroup.getGroupId()).setShortName(testGroup.getShortName())
                .setLongName(testGroup.getLongName()).build())
                .when(groupService).getGroupDetails(testGroup.getGroupId());
        when(groupSettingsService.getGroupSettingsByGroupId(testGroup.getGroupId())).thenReturn(testGroupSettings);

        mockMvc.perform(get("/groupSettings/refreshGroupSettings").param("groupId", Integer.toString(testGroup.getGroupId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("groupShortName", testGroup.getShortName()))
                .andExpect(model().attribute("groupLongName", testGroup.getLongName()))
                .andExpect(model().attribute("repoName", testGroupSettings.getRepoName()));
    }

    /**
     * Test that the endpoint return a model which contains commits, also check that when
     * selected Branch Name is All Branches and selected user is All Users, we called getCommits() function
     * from GitlabApi Service with value of null for branchName parameter and userEmail parameter
     * @throws Exception when an exception is thrown while performing the get request
     */
    @Test
    void getCommitsInSpecificBranchAndFromSpecificUsers() throws Exception {
        ObjectNotFoundException exception = new ObjectNotFoundException(testGroup.getGroupId(), "test");
        String userEmail = "test@test.com";
        String branchName = "test Branch";
        ArgumentCaptor<String> branchNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);

        when(gitLabApiService.getCommits(eq(testGroup.getGroupId()), any(String.class),any(String.class))).thenThrow(exception);

        mockMvc.perform(get("/repository-commits").param("groupId", Integer.toString(testGroup.getGroupId())).param("branchName", branchName).param("userEmail", userEmail))
                .andExpect(status().isOk());

        verify(gitLabApiService, times(1)).getCommits(eq(testGroup.getGroupId()),branchNameCaptor.capture(), userCaptor.capture());
        assertEquals(branchName, branchNameCaptor.getValue());
        assertEquals(userEmail, userCaptor.getValue());

    }

    /**
     * Test that a post request is sent to update the group's Long name but the system fails to update the long name.
     * This test expect that the system will return a model with the error message. it also expects that the system will return 400 status code and return groupLongNameAlertBanner fragment.
     * @throws Exception when an exception is thrown while performing the post request
     */
    @Test
    void modifyGroupLongNameButUnableToUpdate() throws Exception {
        ModifyGroupDetailsResponse response = ModifyGroupDetailsResponse.newBuilder()
                .setIsSuccess(false).setMessage("Unable to update group long name").build();
        doReturn(response).when(groupService).editGroupDetails(any(Integer.class),any(String.class), any(String.class));
        when(permissionService.isValidToModifyGroupSettingPage(any(Integer.class), any(Integer.class))).thenReturn(true);
        doReturn(true).when(groupSettingsService).isValidGroupSettings((int)testGroupSettings.getRepoId(),
                testGroupSettings.getRepoName(), testGroupSettings.getRepoApiKey());

        mockMvc.perform(post("/saveGroupSettings")
                        .param("groupLongName", "newLongName")
                        .param("groupShortName", testGroup.getShortName())
                        .param("groupId", Integer.toString(testGroup.getGroupId()))
                        .param("repoName", testGroupSettings.getRepoName())
                        .param("repoID", Integer.toString((int) testGroupSettings.getRepoId()))
                        .param("repoToken", testGroupSettings.getRepoApiKey())
                        .param("groupSettingsId", Integer.toString(testGroupSettings.getGroupSettingsId())))
                        .andExpect(status().isBadRequest())
                        .andExpect(model().attribute("groupLongNameAlertMessage", "Unable to update group long name"))
                        .andExpect(view().name("groupSettings::groupLongNameAlertBanner"));

        verify(groupService, times(1)).editGroupDetails(testGroup.getGroupId(),testGroup.getShortName(), "newLongName");
    }

    /**
     * Test that a post request to update or set up repository details for a group fails because the system is unable to reach the repository in Gitlab.
     * This test expects that the system will return a model with the error message. it also expects that the system will return 200 status code and return groupSetting fragment.
     * @throws Exception when an exception is thrown while performing the post request
     */
    @Test
    void modifyRepositorySettingButTheRepositoryIsUnreachable() throws Exception {
        ModifyGroupDetailsResponse response = ModifyGroupDetailsResponse.newBuilder()
                .setIsSuccess(true).setMessage("Unable to update group long name").build();
        doReturn(response).when(groupService).editGroupDetails(any(Integer.class),any(String.class), any(String.class));
        doReturn(false).when(gitLabApiService).checkGitLabToken(any(Integer.class), any(String.class), any(String.class));
        doReturn(true).when(groupSettingsService).isGroupSettingSaved(any(Integer.class),any(Integer.class),any(String.class),any(String.class),any(Integer.class), any(String.class));
        doNothing().when(groupService).addGroupDetailToModel(any(Model.class),any(Integer.class));
        doNothing().when(groupSettingsService).addSettingAttributesToModel(any(Model.class), any(GroupSettings.class));
        doNothing().when(groupSettingsController).addGroupSettingAttributeToModel(any(Model.class),any(Integer.class));
        when(permissionService.isValidToModifyGroupSettingPage(any(Integer.class), any(Integer.class))).thenReturn(true);
        doReturn(true).when(groupSettingsService).isValidGroupSettings((int)testGroupSettings.getRepoId(),
                testGroupSettings.getRepoName(), testGroupSettings.getRepoApiKey());

        mockMvc.perform(post("/saveGroupSettings")
                        .param("groupLongName", "newLongName")
                        .param("groupShortName", testGroup.getShortName())
                        .param("groupId", Integer.toString(testGroup.getGroupId()))
                        .param("repoName", testGroupSettings.getRepoName())
                        .param("repoID", Integer.toString((int) testGroupSettings.getRepoId()))
                        .param("repoToken", testGroupSettings.getRepoApiKey())
                        .param("groupSettingsId", Integer.toString(testGroupSettings.getGroupSettingsId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("groupSettingsAlertMessage", "Repository Is Unreachable With The Current Settings"))
                .andExpect(view().name("groupSettings::groupSetting"));

        verify(groupService, times(1)).editGroupDetails(testGroup.getGroupId(),testGroup.getShortName(), "newLongName");
    }

    /**
     * Test that a post request to update or set up repository details for a group fails because the system is unable to reach the repository in Gitlab.
     * This test expects that the system will return a model with the error message. it also expects that the system will return 200 status code and return groupSetting fragment.
     * @throws Exception when an exception is thrown while performing the post request
     */
    @Test
    void modifyRepositorySettingButFailToPersistTheModificationToDatabase() throws Exception {
        ModifyGroupDetailsResponse response = ModifyGroupDetailsResponse.newBuilder()
                .setIsSuccess(true).setMessage("Unable to update group long name").build();
        doReturn(response).when(groupService).editGroupDetails(any(Integer.class),any(String.class), any(String.class));
        doReturn(true).when(gitLabApiService).checkGitLabToken(any(Integer.class), any(String.class), any(String.class));
        doReturn(false).when(groupSettingsService).isGroupSettingSaved(any(Integer.class),any(Integer.class),any(String.class),any(String.class),any(Integer.class),any(String.class));
        doNothing().when(groupService).addGroupDetailToModel(any(Model.class),any(Integer.class));
        doNothing().when(groupSettingsService).addSettingAttributesToModel(any(Model.class), any(GroupSettings.class));
        doNothing().when(groupSettingsController).addGroupSettingAttributeToModel(any(Model.class),any(Integer.class));
        when(permissionService.isValidToModifyGroupSettingPage(any(Integer.class), any(Integer.class))).thenReturn(true);
        doReturn(true).when(groupSettingsService).isValidGroupSettings((int)testGroupSettings.getRepoId(),
                testGroupSettings.getRepoName(), testGroupSettings.getRepoApiKey());

        mockMvc.perform(post("/saveGroupSettings")
                        .param("groupLongName", "newLongName")
                        .param("groupShortName", testGroup.getShortName())
                        .param("groupId", Integer.toString(testGroup.getGroupId()))
                        .param("repoName", testGroupSettings.getRepoName())
                        .param("repoID", Integer.toString((int) testGroupSettings.getRepoId()))
                        .param("repoToken", testGroupSettings.getRepoApiKey())
                        .param("groupSettingsId", Integer.toString(testGroupSettings.getGroupSettingsId())))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("groupSettingsAlertMessage", "Invalid Repository Information"))
                .andExpect(view().name("groupSettings::groupSettingsAlertBanner"));

        verify(groupService, times(1)).editGroupDetails(testGroup.getGroupId(),testGroup.getShortName(), "newLongName");
    }

    /**
     * Tests that a post request to update the repository details returns bad request response with an error banner
     * when the new repository settings are invalid.
     * @throws Exception when an exception is thrown while performing the post request
     */
    @Test
    void modifyRepositorySettingInvalidGroupSettingsArguments() throws Exception {
        when(permissionService.isValidToModifyGroupSettingPage(any(Integer.class), any(Integer.class))).thenReturn(true);
        doReturn(false).when(groupSettingsService).isValidGroupSettings((int)testGroupSettings.getRepoId(),
                testGroupSettings.getRepoName(), testGroupSettings.getRepoApiKey());

        mockMvc.perform(post("/saveGroupSettings")
                        .param("groupLongName", "newLongName")
                        .param("groupShortName", testGroup.getShortName())
                        .param("groupId", Integer.toString(testGroup.getGroupId()))
                        .param("repoName", testGroupSettings.getRepoName())
                        .param("repoID", Integer.toString((int) testGroupSettings.getRepoId()))
                        .param("repoToken", testGroupSettings.getRepoApiKey())
                        .param("groupSettingsId", Integer.toString(testGroupSettings.getGroupSettingsId())))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("groupSettingsAlertMessage", "Please enter valid repository settings"))
                .andExpect(view().name("groupSettings::groupSettingsAlertBanner"));
    }
}