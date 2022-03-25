package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterClientService registerClientService;

    @MockBean
    private LoginController loginController;


    @Test
    void showAccountPage_whenForbidden_return403StatusCode() throws Exception {
        mockMvc.perform(get("/account")).andExpect(status().isForbidden());
    }

//    @Test
//    void showAccountPage_whenAuthorised_return200StatusCode() throws Exception {
//        mockMvc.perform(post("/login").param("usernameLogin","admin").param("passwordLogin", "password"));
//        mockMvc.perform(get("/account").param("userId","1")).andExpect(status().isOk());
//    }

    @Test
    void editAccount_whenForbidden_return403StatusCode() throws Exception {
        mockMvc.perform(post("/backToAccountPage")).andExpect(status().isForbidden());
    }
}