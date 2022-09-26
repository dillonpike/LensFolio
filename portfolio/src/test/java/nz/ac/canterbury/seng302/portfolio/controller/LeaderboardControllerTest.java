package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.LeaderboardEntry;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests controller endpoints related to the leaderboard.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LeaderboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class LeaderboardControllerTest {

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
    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(LeaderboardController.class).build();

    @MockBean
    private UserAccountClientService userAccountClientService;

    @MockBean
    private LeaderboardService leaderboardService;

    @MockBean
    private ElementService elementService;

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
     * Tests the endpoint that returns the leaderboard page.
     * @throws Exception if the request fails
     */
    @Test
    void showLeaderboardPage() throws Exception {
        PaginatedUsersResponse response = PaginatedUsersResponse.newBuilder()
                .addUsers(UserResponse.newBuilder().setId(0).addRoles(UserRole.STUDENT))
                .addUsers(UserResponse.newBuilder().setId(1).addRoles(UserRole.STUDENT))
                .addUsers(UserResponse.newBuilder().setId(2).addRoles(UserRole.STUDENT))
                .build();
        List<UserResponse> usersList = response.getUsersList();
        List<LeaderboardEntry> leaderboardEntries = List.of(new LeaderboardEntry("", "", "", 1, 1, 1));

        when(userAccountClientService.getStudentUsers()).thenReturn(usersList);
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(1);
        doNothing().when(elementService).addHeaderAttributes(any(), anyInt());
        when(leaderboardService.getLeaderboardEntries(usersList)).thenReturn(leaderboardEntries);

        mockMvc.perform(get("/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("leaderboardEntries", leaderboardEntries));
    }
}