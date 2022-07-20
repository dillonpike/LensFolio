package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the DeadlineLifetimeController class.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MilestoneLifetimeController.class)
@AutoConfigureMockMvc(addFilters = false)
class MilestoneLifetimeControllerTest {
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
    private MilestoneService milestoneService;

    @MockBean
    private UserAccountClientService userAccountClientService; // needed to load application

    /**
     * Tests that the milestoneSave controller method can be called with the "/add-milestone" URL and saves the given
     * milestone to the database.
     * @throws Exception when an exception is thrown while performing the post request
     */
    @Test
    void testMilestoneSave() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        Milestone expectedMilestone = new Milestone(0,"Test Milestone", new Date());
        when(milestoneService.addMilestone(any(Milestone.class))).then(returnsFirstArg());

        mockMvc.perform(post("/add-milestone").flashAttr("milestone", expectedMilestone))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/details"));

        verify(milestoneService, times(1)).addMilestone(expectedMilestone);
    }

    /**
     * Tests that the milestoneRemove controller method can be called with the "/delete-milestone" URL and deletes the
     * given milestone from the database.
     * @throws Exception when an exception is thrown while performing the delete request
     */
    @Test
    void testMilestoneRemove() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        Milestone expectedMilestone = new Milestone(0,"Test Milestone", new Date());

        mockMvc.perform(delete("/delete-milestone/" + expectedMilestone.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/details"));

        verify(milestoneService, times(1)).removeMilestone(expectedMilestone.getId());
    }
}