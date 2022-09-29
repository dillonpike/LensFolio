package nz.ac.canterbury.seng302.portfolio.controller.rest;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.Date;

import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Junit testing to test the evidence rest controller
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EvidenceRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class EvidenceRestControllerTest {
    /**
     * AuthState object to be used when we mock security context
     */
    public AuthState validAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("2").build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    @Autowired
    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(EvidenceRestController.class).build();

    @MockBean
    private EvidenceService evidenceService;

    @MockBean
    private ElementService elementService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    /**
     * test to delete the evidence but the evidence is actually not exist.
     * Expect that the controller return 404 not found.
     * @throws Exception
     */
    @Test
    void testDeletingEvidenceWhichDoesNotExist() throws Exception {

        doNothing().when(elementService).addHeaderAttributes(any(Model.class), anyInt());
        when(evidenceService.getEvidence(anyInt())).thenReturn(null);
        mockMvc.perform(post("/delete-evidence/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * test to delete the evidence of other user.
     * Expect that the controller return 403 forbidden.
     * @throws Exception
     */
    @Test
    void testDeletingEvidenceOfOtherUser() throws Exception {
        Evidence testEvidence = new Evidence(0,2,"test evidence", "test description", new Date());

        doNothing().when(elementService).addHeaderAttributes(any(Model.class), anyInt());
        when(evidenceService.getEvidence(anyInt())).thenReturn(testEvidence);
        mockMvc.perform(post("/delete-evidence/1"))
                .andExpect(status().isForbidden());
    }

    /**
     * test to delete the evidence of the user for something reason the evidence is failed to be deleted.
     * Expect that the controller return 500 internal server error.
     * @throws Exception
     */
    @Test
    void testDeletingEvidenceButHaveInternalFailure() throws Exception {
        Evidence testEvidence = new Evidence(0,0,"test evidence", "test description", new Date());
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(0);
        doNothing().when(elementService).addHeaderAttributes(any(Model.class), anyInt());
        when(evidenceService.getEvidence(anyInt())).thenReturn(testEvidence);
        when(evidenceService.removeEvidence(anyInt())).thenReturn(false);
        mockMvc.perform(post("/delete-evidence/1"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * test to delete the evidence of the user and it successfully deleted.
     * Expect that the controller return 200 ok.
     * @throws Exception
     */
    @Test
    void testDeletingEvidenceWithNoProblem() throws Exception {
        Evidence testEvidence = new Evidence(0,0,"test evidence", "test description", new Date());
        when(userAccountClientService.getUserIDFromAuthState(any(AuthState.class))).thenReturn(0);
        doNothing().when(elementService).addHeaderAttributes(any(Model.class), anyInt());
        when(evidenceService.getEvidence(anyInt())).thenReturn(testEvidence);
        when(evidenceService.removeEvidence(anyInt())).thenReturn(true);
        mockMvc.perform(post("/delete-evidence/1"))
                .andExpect(status().isOk());
    }
}
