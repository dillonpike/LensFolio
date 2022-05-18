package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisteringStepDefs {

    private WebDriver webDriver;
    private WebDriverWait wait;
    private Boolean alreadyRegistered = false;
    private String outcome = "";
    private String address = "http://localhost:9000/login";

    private int userId;

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
        webDriver.navigate().to(address);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
    }

    @When("I am on the login page")
    public void iAmOnTheLoginPage() {
        webDriver.navigate().to(address);
    }

    @Then("I can login or register")
    public void iCanLoginOrRegister() {
        assertTrue(webDriver.findElement(By.id("signIn")).isDisplayed());
        assertTrue(webDriver.findElement(By.id("registerNow")).isDisplayed());
    }

    @When("I am on the register page")
    public void iAmOnTheRegisterPage() {
        address = "http://localhost:9000/register";
        webDriver.navigate().to(address);
    }

    @Then("Mandatory fields are marked")
    public void mandatoryFieldsAreMarked() {
        String username = webDriver.findElement(By.id("username")).getAttribute("required");
        assertNotNull(username);

        String firstName = webDriver.findElement(By.id("firstName")).getAttribute("required");
        assertNotNull(firstName);

        String lastName = webDriver.findElement(By.id("lastName")).getAttribute("required");
        assertNotNull(lastName);

        String email = webDriver.findElement(By.id("email")).getAttribute("required");
        assertNotNull(email);

        String passwordLogin = webDriver.findElement(By.id("passwordLogin")).getAttribute("required");
        assertNotNull(passwordLogin);

        String confirmPassword = webDriver.findElement(By.id("confirmPassword")).getAttribute("required");
        assertNotNull(confirmPassword);
    }

    @When("I register with a username {string}")
    public void iRegisterWithAUsernameUsername(String username) {
        address = "http://localhost:9000/register";
        webDriver.navigate().to(address);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
        webDriver.findElement(By.id("username")).sendKeys(username);
        webDriver.findElement(By.id("firstName")).sendKeys("Admin");
        webDriver.findElement(By.id("lastName")).sendKeys("Admin");
        webDriver.findElement(By.id("email")).sendKeys("name@example.com");
        webDriver.findElement(By.id("passwordLogin")).sendKeys("Admin");
        webDriver.findElement(By.id("confirmPassword")).sendKeys("Admin");
        WebElement ele = webDriver.findElement(By.id("signUp"));
        JavascriptExecutor jse = (JavascriptExecutor)webDriver;
        jse.executeScript("arguments[0].click()", ele);
    }

    @And("Username is already registered {string}")
    public void usernameIsAlreadyRegisteredIsAlreadyRegistered(String alreadyReg) {
        if (Objects.equals(alreadyReg, "True")) {
            alreadyRegistered = true;
            if (Objects.equals(address, "http://localhost:9000/register")) {
                outcome = "Invalid registration, username taken";
            } else {
                outcome = "Logged in";
            }
        } else {
            if (Objects.equals(address, "http://localhost:9000/register")) {
                outcome = "Successful registration";
            } else {
                outcome = "Invalid username, please try again";
            }
        }
    }

    @Then("{string} message occurs")
    public void outcomeMessageOccurs(String outcomeMessage) {
        if (Objects.equals(outcome, "Invalid registration, username taken")){
            Boolean outcomeMes = webDriver.findElement(By.id("usernameTaken")).isDisplayed();
            assertNotNull(outcomeMes);
        } else if (Objects.equals(outcome, "Successful registration")) {
            String actualURL = webDriver.getCurrentUrl();
            assertNotEquals("http://localhost:9000/register?registerError", actualURL);
        } else if (Objects.equals(outcome, "Invalid username, please try again")) {
            Boolean outcomeMes = webDriver.findElement(By.id("usernameInvalidMessage")).isDisplayed();
            assertNotNull(outcomeMes);
        } else if (Objects.equals(outcome, "First name can not contain numbers or special characters")) {
            Boolean outcomeMes = webDriver.findElement(By.id("firstNameInvalid")).isDisplayed();
            assertNotNull(outcomeMes);
        }

    }

    @And("Username is registered {string}")
    public void usernameIsRegisteredIsRegistered(String reg) {
        String isReg = "True";
        if (alreadyRegistered) {
            isReg = "False";
        }
        assertEquals(isReg, reg);

    }

    @When("I login with a username {string}")
    public void iLoginWithAUsernameUsername(String username) {
        address = "http://localhost:9000/login";
        webDriver.navigate().to(address);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
        webDriver.findElement(By.id("usernameLogin")).sendKeys(username);
        webDriver.findElement(By.id("passwordLogin")).sendKeys("password");
        webDriver.findElement(By.id("signIn")).click();
        try {
            WebDriverWait customWait = new WebDriverWait(webDriver, 2); // 2 second wait time
            customWait.until(ExpectedConditions.elementToBeClickable(By.id("editProfileButton")));
            userId = Integer.parseInt(webDriver.findElement(By.id("userId")).getAttribute("value"));
        } catch (Exception e) {
            userId = -1;
        }
    }

    @And("Username is logged in {string}")
    public void usernameIsLoggedInIsLoggedIn(String loggedIn) {
        if (alreadyRegistered) {
            String actualURL = webDriver.getCurrentUrl();
            assertNotEquals("http://localhost:9000/login", actualURL);
        }
    }

    @When("I login with a  wrong username {string}")
    public void iLoginWithAWrongUsername(String username) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
        webDriver.findElement(By.id("usernameLogin")).sendKeys(username);
    }

    @And("I login with any password")
    public void iLoginWithAnyPassword() throws InterruptedException {
        webDriver.findElement(By.id("passwordLogin")).sendKeys("anyPassword");
        webDriver.findElement(By.id("signIn")).click();
        Thread.sleep(2000);
    }

    @Then("username error message should be displayed")
    public void usernameErrorMessageShouldBeDisplayed() {
        assertTrue(webDriver.findElement(By.id("usernameInvalidMessage")).isDisplayed());
    }

    @When("I login with a  right username {string}")
    public void iLoginWithARightUsername(String username) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
        webDriver.findElement(By.id("usernameLogin")).sendKeys(username);
    }

    @And("I login with wrong password {string}")
    public void iLoginWithWrongPassword(String password) throws InterruptedException {
        webDriver.findElement(By.id("passwordLogin")).sendKeys(password);
        webDriver.findElement(By.id("signIn")).click();
        Thread.sleep(2000);

    }

    @Then("password error message should be displayed")
    public void passwordErrorMessageShouldBeDisplayed() {
        assertTrue(webDriver.findElement(By.id("passwordInvalidMessage")).isDisplayed());
    }

    @Then("I am taken to my account page")
    public void iAmTakenToMyAccountPage() {
        String actualURL = webDriver.getCurrentUrl();
        assertNotEquals("http://localhost:9000/login", actualURL);
    }

    @And("I can view my details")
    public void iCanViewMyDetails() {
        assertTrue(webDriver.findElement(By.id("firstName")).isDisplayed());
        assertTrue(webDriver.findElement(By.id("lastNameInput")).isDisplayed());
        assertEquals(userId, Integer.parseInt(webDriver.findElement(By.id("userId")).getAttribute("value")));
    }

  @When("I register with a first name {string}")
  public void iRegisterWithAFirstNameFirstName(String firstName) {
      webDriver.findElement(By.id("username")).sendKeys("adminTests");
      webDriver.findElement(By.id("firstName")).sendKeys(firstName);
      webDriver.findElement(By.id("lastName")).sendKeys("admin");
      WebElement ele = webDriver.findElement(By.id("signUp"));
      JavascriptExecutor jse = (JavascriptExecutor)webDriver;
      jse.executeScript("arguments[0].click()", ele);
  }
}
