package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * Junit testing to test the Calendar Controller
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CalendarController.class)
@AutoConfigureMockMvc(addFilters = false)
class CalendarControllerTest {

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
    private SprintService sprintService;

    @MockBean
    private ElementService elementService;

    @MockBean
    private EventService eventService;

    @MockBean
    private DeadlineService deadlineService;

    @MockBean
    private MilestoneService milestoneService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(CalendarController.class).build();
    }

    /**
     * unit testing to test the get method when calling "/calendar"
     * Expect to return 200 status code and calendar page with some project's and sprint's information in the model
     */
    @Test
    void testGetCalendarMethod_return200StatusCode_andCalendarPage() throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        String date_string = "16-09-2021";
        Date firstStartDate = formatter.parse(date_string);

        String second_date_string = "18-09-2021";
        Date firstFinishDate = formatter.parse(second_date_string);

        String third_date_string = "23-09-2021";
        Date secondStartDate = formatter.parse(third_date_string);

        String fourth_date_string = "26-09-2021";
        Date secondFinishDate = formatter.parse(fourth_date_string);

        Sprint firstSprint = new Sprint(1,"firstSprint", "Label 1", "test1", firstStartDate,firstFinishDate);
        Sprint secondSprint = new Sprint(2,"secondSprint", "Label 2", "test1", secondStartDate, secondFinishDate);

        List<Sprint> sprints = Arrays.asList(firstSprint, secondSprint);


        String fifth_date_string = "26-08-2021";
        Date projectStartDate = formatter.parse(fifth_date_string);

        String sixth_date_string = "26-10-2021";
        Date projectFinishDate = formatter.parse(sixth_date_string);

        Project project = new Project("testProject", "test", projectStartDate, projectFinishDate);

        String expected = "{id: '0', title: 'firstSprint', start: 'Thu Sep 16 00:00:00 NZST 2021', end: '2021-09-18T12:00:00Z', allDay: true, color: '#5897fc', type: 'Sprint'},{id: '0', title: 'secondSprint', start: 'Thu Sep 23 00:00:00 NZST 2021', end: '2021-09-26T11:00:00Z', allDay: true, color: '#a758fc', type: 'Sprint'},";

        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        SecurityContextHolder.setContext(mockedSecurityContext);

        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(1);
        when(sprintService.getAllSprintsOrdered()).thenReturn(sprints);
        when(projectService.getProjectById(any(Integer.class))).thenReturn(project);

        mockMvc.perform(get("/calendar"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("calendar")) // Whether to return the template "account"
                .andExpect(model().attribute("events", expected))
                .andExpect(model().attribute("userId", 1))
                .andExpect(model().attribute("projectName", "testProject"))
                .andExpect(model().attribute("currentUserRole", "ADMIN"));
    }

}