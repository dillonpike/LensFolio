package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.PermissionService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/***
 * Testing class which contains unit test for methods in EditDeadlineController class
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EditDeadlineController.class)
@AutoConfigureMockMvc(addFilters = false)
class EditDeadlineControllerTest {

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
    private DeadlineService deadlineService;

    @MockBean
    private UserAccountClientService userAccountClientService; // needed to load application

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ElementService elementService;

    /***
     * Test the post request method to edit deadline
     * This only purpose of this test is to check whether the controller is called, and it called the correct method in
     * the service class
     * Expect to return 302 status code(redirection) and verify the updateDeadline function in deadlinesService is called
     */
    @Test
    void sendRequestToEditDeadline() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        SecurityContextHolder.setContext(mockedSecurityContext);

        String expectedDeadlineName = "Test Deadline";
        Date expectedDeadlineDate = new Date();

        Deadline newDeadline = new Deadline(0, expectedDeadlineName, expectedDeadlineDate);
        when(permissionService.isValidToModifyProjectPage(any(Integer.class))).thenReturn(true);
        when(deadlineService.getDeadlineById(any(Integer.class))).thenReturn(newDeadline);
        when(deadlineService.updateDeadline(any(Deadline.class))).then(returnsFirstArg());

        ArgumentCaptor<Deadline> deadlinesArgumentCaptor = ArgumentCaptor.forClass(Deadline.class);
        mockMvc.perform(post("/edit-deadline/{id}", newDeadline.getId()).flashAttr("deadline", newDeadline))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/details"));

        Mockito.verify(deadlineService).updateDeadline(deadlinesArgumentCaptor.capture());
        Deadline addedDeadline = deadlinesArgumentCaptor.getValue();
        assertEquals(expectedDeadlineName, addedDeadline.getDeadlineName());
        assertEquals(expectedDeadlineDate, addedDeadline.getDeadlineDate());
    }
}