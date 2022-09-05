package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.PortfolioApplication;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the evidence controller.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
class EvidenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvidenceService evidenceService;

    @MockBean
    private ElementService elementService; // needed to load application context

    @MockBean
    private UserAccountClientService userAccountClientService; // needed to load application context

    @MockBean
    private RegisterClientService registerClientService; // needed to load application context

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

    private static final String ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE = "evidenceTitleAlertMessage";

    private static final String EVIDENCE_HTTP_CONTENT = "evidenceTitle=Test+Evidence&evidenceDescription=Test+Description&evidenceDate=12%2FDec%2F2021";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(EvidenceController.class).build();

        // Mock the security context
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(1);
        when(registerClientService.getUserData(1)).thenReturn(mockUser);
    }

    /**
     * Tests blue sky scenario for adding evidence.
     * @throws Exception If mocking the MVC fails.
     */
    @Test
    void testAddEvidence200() throws Exception {
        when(evidenceService.addEvidence(any(Evidence.class))).thenReturn(true);

        mockMvc.perform(post("/add-evidence").contentType(MediaType.APPLICATION_JSON).content(EVIDENCE_HTTP_CONTENT)).andExpect(status().isOk())
                .andExpect(model().attribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, "Evidence Added. "));

        verify(evidenceService, times(1)).addEvidence(any(Evidence.class));
    }

    /**
     * Tests adding evidence failing due to the evidence service failing to add the evidence.
     * @throws Exception If mocking the MVC fails.
     */
    @Test
    void testAddEvidence500() throws Exception {
        when(evidenceService.addEvidence(any(Evidence.class))).thenReturn(false);

        mockMvc.perform(post("/add-evidence").contentType(MediaType.APPLICATION_JSON).content(EVIDENCE_HTTP_CONTENT)).andExpect(status().isInternalServerError())
                .andExpect(model().attribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, "Evidence Not Added. Saving Error Occurred."));

        verify(evidenceService, times(1)).addEvidence(any(Evidence.class));
    }

    /**
     * Tests adding evidence failing due to the evidence data missing.
     * @throws Exception If mocking the MVC fails.
     */
    @Test
    void testAddEvidence400MissingData() throws Exception {
        mockMvc.perform(post("/add-evidence").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest())
                .andExpect(model().attribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, "Evidence Not Added. Following fields are required: 'title' 'description' 'date'. "));

        verify(evidenceService, times(0)).addEvidence(any(Evidence.class));
    }

    /**
     * Tests adding evidence failing due to the evidence data not including a date.
     * @throws Exception If mocking the MVC fails.
     */
    @Test
    void testAddEvidence400MissingDate() throws Exception {

        String evidenceHttpContentMissingDate = "evidenceTitle=Test+Evidence&evidenceDescription=Test+Description";

        mockMvc.perform(post("/add-evidence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evidenceHttpContentMissingDate)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, "Evidence Not Added. Following fields are required: 'date'. "));

        verify(evidenceService, times(0)).addEvidence(any(Evidence.class));
    }
}
