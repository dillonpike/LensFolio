package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisteringStepDefs {

    private WebDriver webDriver;
    private WebDriverWait wait;
    private Boolean alreadyRegistered = false;
    private String outcome = "";

    @Before
    public void setUp() {
        webDriver = SeleniumService.getWebDriver();
        wait = SeleniumService.getWait();
    }

    @After
    public void tearDown() {
        SeleniumService.tearDownWebDriver();
    }

    @Given("I am not logged in")
    public void iAmNotLoggedIn() {
        webDriver.navigate().to("http://localhost:9000/login");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
    }

    @When("I am on the login page")
    public void iAmOnTheLoginPage() {
        webDriver.navigate().to("http://localhost:9000/login");
    }

    @Then("I can login or register")
    public void iCanLoginOrRegister() {
        assertTrue(webDriver.findElement(By.id("signIn")).isDisplayed());
        assertTrue(webDriver.findElement(By.id("registerNow")).isDisplayed());
    }

    @When("I am on the register page")
    public void iAmOnTheRegisterPage() {
        webDriver.navigate().to("http://localhost:9000/register");
    }

    @Then("Mandatory fields are marked")
    public void mandatoryFieldsAreMarked() {
        String firstName = webDriver.findElement(By.id("firstName")).getAttribute("required");
        assertEquals("True", firstName);

        String lastName = webDriver.findElement(By.id("lastName")).getAttribute("required");
        assertEquals("True", lastName);

        String email = webDriver.findElement(By.id("email")).getAttribute("required");
        assertEquals("True", email);

        String passwordLogin = webDriver.findElement(By.id("passwordLogin")).getAttribute("required");
        assertEquals("True", passwordLogin);

        String confirmPassword = webDriver.findElement(By.id("confirmPassword")).getAttribute("required");
        assertEquals("True", confirmPassword);
    }

    @When("I register with a username {String}")
    public void iRegisterWithAUsernameUsername(String username) {
        webDriver.navigate().to("http://localhost:9000/register");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("signUp")));
        webDriver.findElement(By.id("username")).sendKeys(username);
    }
    @When("I register with a username <Username>")
    public void iRegisterWithAUsernameUsername() {
    }

    @And("Username is already registered {String}")
    public void usernameIsAlreadyRegisteredIsAlreadyRegistered(String alreadyReg) {
        if (Objects.equals(alreadyReg, "True")) {
            alreadyRegistered = true;
            outcome = "Username already registered";
        } else {
            outcome = "Registered";
        }
    }

    @Then("{String} message occurs")
    public void outcomeMessageOccurs(String outcomeMessage) {
        assertEquals(outcome, outcomeMessage);
    }

    @And("Username is registered {String}")
    public void usernameIsRegisteredIsRegistered(String reg) {
        String isReg = "True";
        if (alreadyRegistered) {
            isReg = "False";
        }
        assertEquals(isReg, reg);

    }

    @When("I login with a username <Username>")
    public void iLoginWithAUsernameUsername() {
        
    }

    @And("Username is logged in <isLoggedIn>")
    public void usernameIsLoggedInIsLoggedIn() {
    }
}
