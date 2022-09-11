package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
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
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Junit testing to test the View User Controller
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ViewUsersController.class)
@AutoConfigureMockMvc(addFilters = false)
class ViewUsersControllerTest {

    @Autowired
    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(AccountController.class).build();

    @MockBean
    private UserAccountClientService userAccountClientService;

    @MockBean
    private ElementService elementService;

    @MockBean
    private RegisterClientService registerClientService;

    @MockBean
    private UserSortingService userSortingService;

    @MockBean
    private PermissionService permissionService;


    private UserResponse userResponse;

    private final int USER_ID = 1;
    private final String USERNAME = "Username";

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
     * Mocked user response which contains the data of the user1
     */
    private final UserResponse mockUser1 = UserResponse.newBuilder()
            .setBio("default bio")
            .setCreated(Timestamp.newBuilder().setSeconds(55))
            .setEmail("hello@test.com")
            .setFirstName("firsttestname")
            .setLastName("lasttestname")
            .setMiddleName("middlettestname")
            .setNickname("niktestname")
            .setPersonalPronouns("He/him")
            .addRoles(UserRole.STUDENT)
            .addRoles(UserRole.COURSE_ADMINISTRATOR)
            .build();
    /**
     * Mocked user response which contains the data of the user2
     */
    private final UserResponse mockUser2 = UserResponse.newBuilder()
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

    public PaginatedUsersResponse mockedUserList = PaginatedUsersResponse.newBuilder()
            .addUsers(mockUser1).addUsers(mockUser2).build();

    /**
     * Test the GET method when calling "viewUser"
     * Except to catch status code 200
     */
    @Test
    void showViewUserPage_whenLoggedIn_return200StatusCode() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        userResponse = UserResponse.newBuilder().setUsername(USERNAME).setId(USER_ID).build();
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(USER_ID);
        when(registerClientService.getUserData(any(Integer.class))).thenReturn(userResponse);

        SecurityContextHolder.setContext(mockedSecurityContext);
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(1);
        when(userAccountClientService.getAllUsers()).thenReturn(mockedUserList);

        mockMvc.perform(get("/viewUsers"))
                .andExpect(status().isOk());
    }

    /**
     * Test to check if model attribute(users) has been added in view users page
     */
    @Test
    void showViewUserPage_whenLoggedIn_returnUsersList() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        userResponse = UserResponse.newBuilder().setUsername(USERNAME).setId(USER_ID).build();
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(USER_ID);
        when(registerClientService.getUserData(any(Integer.class))).thenReturn(userResponse);

        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/viewUsers"));

        verify(elementService).addUsersToModel(any(Model.class), any(Integer.class));
    }

    /**
     * Test to check when logged In, calling view user page will return viewUsers template
     */
    @Test
    void showViewUserPage_whenLoggedIn_returnViewUserTemplate() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        userResponse = UserResponse.newBuilder().setUsername(USERNAME).setId(USER_ID).build();
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(USER_ID);
        when(registerClientService.getUserData(any(Integer.class))).thenReturn(userResponse);

        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/viewUsers"))
                .andExpect(view().name("viewUsers"));
    }

    /**
     * Test to check when logged In, calling view user page will return viewUsers template
     */
    @Test
    void callDeleteRoleRequest_whenLoggedInAsCourseAdministrator_returnViewUserTemplate() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        userResponse = UserResponse.newBuilder().setUsername(USERNAME).setId(USER_ID).build();
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(USER_ID);
        when(registerClientService.getUserData(any(Integer.class))).thenReturn(userResponse);

        SecurityContextHolder.setContext(mockedSecurityContext);

        UserRoleChangeResponse response = UserRoleChangeResponse.newBuilder()
                .setIsSuccess(true)
                .build();

        when(userAccountClientService.deleteRoleFromUser(any(Integer.class),any(UserRole.class))).thenReturn(response);
        mockMvc.perform(post("/delete_role").param("userId","1").param("deletedRole","STUDENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("viewUsers"));
    }

    /**
     * Junit test to check if current user log in as a course administrator and request add role request to other user
     */
    @Test
    void callAddRoleRequest_whenLoggedInAsCourseAdministrator_returnViewUserTemplate() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        userResponse = UserResponse.newBuilder().setUsername(USERNAME).setId(USER_ID).build();
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(USER_ID);
        when(registerClientService.getUserData(any(Integer.class))).thenReturn(userResponse);

        SecurityContextHolder.setContext(mockedSecurityContext);

        UserRoleChangeResponse response = UserRoleChangeResponse.newBuilder()
                .setIsSuccess(true)
                .build();

        when(userAccountClientService.deleteRoleFromUser(any(Integer.class),any(UserRole.class))).thenReturn(response);
        mockMvc.perform(post("/add_role").param("userId","1").param("role","STUDENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("viewUsers"));
    }

}
