package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium Cucumber step definitions for the roles feature.
 */
public class RolesStepDefs {

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

    @Then("My account page displays the following roles: {string}")
    public void myAccountPageDisplaysTheFollowingRoles(String rolesString) {
        for (String role: rolesString.split(", ")) {
            assertTrue(webDriver.findElement(By.xpath("//h6[" + SeleniumService.XPATH_LOWER_CASE_TEXT + "='" +
                    role.toLowerCase() + "']")).isDisplayed());
        }
    }

    @Given("I can login or register with a username {string}")
    public void iCanLoginOrRegisterWithAUsername(String username) {
        webDriver.navigate().to("http://localhost:9000/login");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
        webDriver.findElement(By.id("usernameLogin")).sendKeys(username);
        webDriver.findElement(By.id("passwordLogin")).sendKeys("password");
        webDriver.findElement(By.id("signIn")).click();
        try {
            WebDriverWait customWait = new WebDriverWait(webDriver, 2); // 2 seconds wait time
            customWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(., 'Profile')]")));
        } catch (Exception e) {
            webDriver.navigate().to("http://localhost:9000/register");
            wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
            webDriver.findElement(By.id("username")).sendKeys(username);
            webDriver.findElement(By.id("firstName")).sendKeys("roles_test");
            webDriver.findElement(By.id("lastName")).sendKeys("Last");
            webDriver.findElement(By.id("email")).sendKeys("name@example.com");
            webDriver.findElement(By.id("passwordLogin")).sendKeys("password");
            webDriver.findElement(By.id("confirmPassword")).sendKeys("password");
            WebElement ele = webDriver.findElement(By.id("signUp"));
            JavascriptExecutor jse = (JavascriptExecutor)webDriver;
            jse.executeScript("arguments[0].click()", ele);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(., 'Profile')]")));
    }
}
