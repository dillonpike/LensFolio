package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * Junit testing to test the Register Controller
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(RegisterController.class).build();

    @MockBean
    private RegisterClientService registerClientService;

    @MockBean
    private AuthenticateClientService authenticateClientService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    /**
     * Test get method of register controller
     * Expect to return 200 status code also the registration page which come with default user's information
     */
    @Test
    void showRegistrationPage_givenDefaultInformation_return200StatusCode_andRegisterPage() throws Exception {
        String defaultTestFirstName = "defaultTestFirstName";
        String defaultTestMiddleName = "defaultTestMiddleName";
        String defaultTestLastName = "defaultTestLastName";
        String defaultTestEmail = "test@email.com";
        String defaultTestUsername = "defaultTestUsername";



        mockMvc.perform(get("/register").param("defaultFirstName", defaultTestFirstName).param("defaultMiddleName", defaultTestMiddleName).param("defaultLastName", defaultTestLastName).param("defaultEmail", defaultTestEmail).param("defaultUsername", defaultTestUsername))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("defaultFirstName", defaultTestFirstName))
                .andExpect(model().attribute("defaultMiddleName", defaultTestMiddleName))
                .andExpect(model().attribute("defaultLastName", defaultTestLastName))
                .andExpect(model().attribute("defaultEmail", defaultTestEmail))
                .andExpect(model().attribute("defaultUsername", defaultTestUsername));
    }

}