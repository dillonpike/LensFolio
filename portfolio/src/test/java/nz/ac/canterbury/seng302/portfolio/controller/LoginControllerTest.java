package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Junit testing to test the Login Controller
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(LoginController.class).build();

    @MockBean
    private AuthenticateClientService authenticateClientService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    /**
     * Test get method of login controller
     * Expect to return 200 status code also the login page
     */
    @Test
    void showLoginPage_whenBrowseToSlashLogin_return200StatusCode_andRegisterPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("login"));
    }

    /**
     * Test get method of login controller
     * When browsing to "/"
     * Expect to return 302 status code an then it will redirect to get method /login
     */
    @Test
    void showLoginPage_whenBrowseToSlash_return200StatusCode_andRegisterPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection()) // Whether to return the status "302"
                .andExpect(redirectedUrl("login"));
    }


}