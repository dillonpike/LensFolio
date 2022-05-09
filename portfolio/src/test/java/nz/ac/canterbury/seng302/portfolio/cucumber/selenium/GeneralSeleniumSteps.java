package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * General Selenium Cucumber step definitions that are relevant for multiple features.
 */
public class GeneralSeleniumSteps {

    /**
     * Webdriver used during tests.
     */
    private WebDriver webDriver;

    /**
     * WebDriverWait object that is used to wait until some criteria is met, for example an element to be visible.
     */
    private WebDriverWait wait;

    /**
     * Sets up for scenario by getting a web driver and WebDriverWait object.
     */
    @Before
    public void setUp() {
        webDriver = SeleniumService.getWebDriver();
        wait = SeleniumService.getWait();
    }

    /**
     * Tears down after running scenario by quitting the web driver (thus closing the browser) and setting the web
     * driver to null.
     */
    @After
    public void tearDown() {
        SeleniumService.tearDownWebDriver();
    }

    @When("I log in as admin")
    public void iLogInAsAdmin() {
        iAmLoggedInAsUsername("admin");
    }

    @Given("I am logged in as admin")
    public void iAmLoggedInAsAdmin() {
        iLogInAsAdmin();
    }

    @And("I log out")
    public void iLogOut() {
        webDriver.findElement(By.id("dropdownUser1")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("signOutButton")));
        webDriver.findElement(By.id("signOutButton")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
    }

    @When("I browse to the account page")
    public void iBrowseToTheAccountPage() {
        webDriver.findElement(By.id("dropdownUser1")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("profileButton")));
        webDriver.findElement(By.id("profileButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(., 'Profile')]")));
    }

    @When("I browse to the project page")
    public void iBrowseToTheProjectPage() {
        webDriver.findElement(By.id("projectsHeaderButton")).click();
    }

    @Given("I am logged in as {string}")
    public void iAmLoggedInAsUsername(String username) {
        webDriver.navigate().to("http://localhost:9000/login");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
        webDriver.findElement(By.id("usernameLogin")).sendKeys(username);
        webDriver.findElement(By.id("passwordLogin")).sendKeys("password");
        webDriver.findElement(By.id("signIn")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h2[contains(., 'Profile')]")));
    }

    @And("I am on the edit account page")
    public void iAmOnTheEditAccountPage() {
        iBrowseToTheAccountPage();
        webDriver.findElement(By.id("editProfileButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(., 'Edit Profile')]")));
    }
}
