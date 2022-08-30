package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
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

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Junit testing to test the Account Controller
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {
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
    private UserResponse mockUser = UserResponse.newBuilder()
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

    /**
     * Mocked project
     */
    private Project mockProject = new Project("test project", "test description", new Date(), new Date());


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterClientService registerClientService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @MockBean
    private ElementService elementService;

    @MockBean
    private PhotoService photoService;

    @MockBean
    private ProjectService projectService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(AccountController.class).build();
    }

    /**
     * unit testing to test the get method when calling "/account"
     * Expect to return 200 status code and account page with some user's information in the model
     */
    @Test
    void showAccountPage_whenLoggedIn_return200StatusCode_andAccountPage() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        SecurityContextHolder.setContext(mockedSecurityContext);
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(1);
        when(registerClientService.getUserData(1)).thenReturn(mockUser);
        when(projectService.getProjectById(0)).thenReturn(mockProject);

        String firstName = mockUser.getFirstName();
        String middleName = mockUser.getMiddleName();
        String lastName = mockUser.getLastName();
        String nickName = mockUser.getNickname();
        String username = mockUser.getUsername();
        String email = mockUser.getEmail();
        String bio = mockUser.getBio();
        String roles = "STUDENT";
        String fullName = firstName + " " + middleName + " " + lastName;
        String personalPronouns = mockUser.getPersonalPronouns();


        mockMvc.perform(get("/account").param("userId", "1"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("account")) // Whether to return the template "account"
                .andExpect(model().attribute("firstName", firstName))
                .andExpect(model().attribute("lastName", lastName))
                .andExpect(model().attribute("middleName", middleName))
                .andExpect(model().attribute("fullName", fullName))
                .andExpect(model().attribute("nickName", nickName))
                .andExpect(model().attribute("username", username))
                .andExpect(model().attribute("email", email))
                .andExpect(model().attribute("personalPronouns", personalPronouns))
                .andExpect(model().attribute("bio", bio))
                .andExpect(model().attribute("project", mockProject));
    }

}