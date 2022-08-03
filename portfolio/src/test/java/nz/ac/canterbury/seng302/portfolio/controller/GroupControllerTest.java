package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

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
    private GroupService groupService;

    @MockBean
    private ElementService elementService; // needed to load application context

    @MockBean
    private UserAccountClientService userAccountClientService; // needed to load application context

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(GroupController.class).build();
    }

    @Test
    void testDeleteExistingGroup() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        int expectedGroupId = 1;
        when(groupService.deleteGroup(expectedGroupId)).thenReturn(DeleteGroupResponse.newBuilder().setIsSuccess(true).build());

        mockMvc.perform(get("/delete-group/" + expectedGroupId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/group?isUpdateSuccess=true"))
                .andExpect(model().attribute("isUpdateSuccess", "true"));

        verify(groupService, times(1)).deleteGroup(expectedGroupId);
    }

    @Test
    void testDeleteNonExistingGroup() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        int expectedGroupId = 1;
        when(groupService.deleteGroup(expectedGroupId)).thenReturn(DeleteGroupResponse.newBuilder().setIsSuccess(false).build());

        mockMvc.perform(get("/delete-group/" + expectedGroupId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/group?isUpdateSuccess=false"))
                .andExpect(model().attribute("isUpdateSuccess", "false"));

        verify(groupService, times(1)).deleteGroup(expectedGroupId);
    }
}