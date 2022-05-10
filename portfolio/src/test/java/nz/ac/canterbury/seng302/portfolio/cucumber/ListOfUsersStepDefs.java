package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponseOrBuilder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.*;

import java.util.Optional;



import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ListOfUsersStepDefs  {

    @Autowired
    private MockMvc mvc;

    private AuthenticateClientService authenticateClientService;

    @Given("I am logged as a user")
    public void i_am_logged_as_a_user() {
        authenticateClientService = Mockito.mock(AuthenticateClientService.class);
        String password = "password";
        String username = "admin";
        AuthenticateResponse.Builder reply = AuthenticateResponse.newBuilder();
        reply
                .setEmail("user.getEmail()")
                .setFirstName("user.getFirstName()")
                .setLastName("user.getLastName()")
                .setMessage("Logged in successfully!")
                .setSuccess(true)
                .setToken("token")
                .setUserId(1)
                .setUsername("user.getUsername()");

        Mockito.when(authenticateClientService.authenticate(anyString(), anyString())).thenReturn(reply.build());
    }
    @When("I browse to list of user page")
    public void i_browse_to_list_of_user_page() {

    }
    @Then("I can see the page that contains list of users registered")
    public void i_can_see_the_page_that_contains_list_of_users_registered() throws Exception {
        mvc = Mockito.mock(MockMvc.class);
        System.out.println(mvc.perform(get("/viewUsers")).andReturn().getResponse());
    }
}
