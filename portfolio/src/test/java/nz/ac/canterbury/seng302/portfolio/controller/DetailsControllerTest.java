package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/***
 * Junit testing to test the project
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DetailsController.class)
@AutoConfigureMockMvc(addFilters = false)
class DetailsControllerTest {

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
    private ProjectService projectService;

    @MockBean
    private SprintService sprintService;

    @MockBean
    private EventService eventService;

    @MockBean
    private DeadlineService deadlineService;

    @MockBean
    private MilestoneService milestoneService;

    @MockBean
    private RegisterClientService registerClientService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @MockBean
    private ElementService elementService;

    public Project mockedProject;

    private UserResponse userResponse;

    private int USER_ID = 1;
    private String USERNAME = "Username";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(AccountController.class).build();
    }

    /***
     * Test to check if project page return 200 status code when send GET method
     */
    @Test
    void showProjectPage_whenLoggedIn_return200StatusCode() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        userResponse = UserResponse.newBuilder().setUsername(USERNAME).setId(USER_ID).build();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Instant time = Instant.now();
        Timestamp dateAdded = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).build();
        LocalDate endDate = time.atZone(ZoneId.systemDefault()).toLocalDate();
        Date date8Months = java.sql.Date.valueOf(endDate.plusMonths(8));  // 8 months after the current date
        mockedProject = new Project(
                "Project " + currentYear,
                "Default Project",
                new Date(dateAdded.getSeconds() * 1000),
                date8Months
        );
        mockedProject.setId(0);

        SecurityContextHolder.setContext(mockedSecurityContext);
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(USER_ID);
        when(registerClientService.getUserData(any(Integer.class))).thenReturn(userResponse);
        when(sprintService.getSuggestedSprint()).thenReturn(new Sprint());
        when(projectService.getProjectById(any(Integer.class))).thenReturn(mockedProject);
        mockMvc.perform(get("/details")).andExpect(status().isOk()); // Whether to return the status "200 OK"
    }

    /***
     * Test to check if model attribute(project, sprint) has been added to project page successfully
     */
    @Test
    void showProjectPage_whenLoggedIn_returnProjectAndSprintExist() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        userResponse = UserResponse.newBuilder().setUsername(USERNAME).setId(USER_ID).build();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Instant time = Instant.now();
        Timestamp dateAdded = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).build();
        LocalDate endDate = time.atZone(ZoneId.systemDefault()).toLocalDate();
        Date date8Months = java.sql.Date.valueOf(endDate.plusMonths(8));  // 8 months after the current date
        mockedProject = new Project(
                "Project " + currentYear,
                "Default Project",
                new Date(dateAdded.getSeconds() * 1000),
                date8Months
        );
        mockedProject.setId(0);

        SecurityContextHolder.setContext(mockedSecurityContext);
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(USER_ID);
        when(registerClientService.getUserData(any(Integer.class))).thenReturn(userResponse);
        when(sprintService.getSuggestedSprint()).thenReturn(new Sprint());
        when(projectService.getProjectById(0)).thenReturn(mockedProject);
        mockMvc.perform(get("/details"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attribute("project", mockedProject))
                .andExpect(model().attributeExists("sprints")); // Whether to return the status "200 OK"
    }
}
