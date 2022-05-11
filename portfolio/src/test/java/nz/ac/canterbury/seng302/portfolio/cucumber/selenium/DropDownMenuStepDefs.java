package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DropDownMenuStepDefs {

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

    @When("I click on the photo icon")
    public void iClickOnThePhotoIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("dropdownUser1")));
        assertTrue(webDriver.findElement(By.id("dropdownUser1")).isDisplayed());
        webDriver.findElement(By.id("dropdownUser1")).click();
    }

    @Then("A menu is displayed")
    public void aMenuIsDisplayed() {
        assertTrue(webDriver.findElement(By.id("dropdownUser1")).isDisplayed());
    }

    @And("There is a logout option")
    public void thereIsALogoutOption() {
        assertTrue(webDriver.findElement(By.id("signOutButton")).isDisplayed());
    }

    @When("I click on the logout button")
    public void iClickOnTheLogoutButton() {
        iClickOnThePhotoIcon();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("signOutButton")));
        assertTrue(webDriver.findElement(By.id("signOutButton")).isDisplayed());
        webDriver.findElement(By.id("signOutButton")).click();
    }

    @When("I try and access my account")
    public void iTryAndAccessMyAccount() {
        webDriver.navigate().back();
    }

    @Then("I am taken to login page")
    public void iAmTakenToLoginPage() {
        String actualURL = webDriver.getCurrentUrl();
        String expectedURL = "http://localhost:9000/login";
        assertEquals(expectedURL, actualURL);
    }

    @Then("I am taken to login page with forbidden error")
    public void iAmTakenToLoginPageWithForbiddenError() {
        String actualURL = webDriver.getCurrentUrl();
        String expectedURL = "http://localhost:9000/login?forbidden";
        assertEquals(expectedURL, actualURL);
    }

}
