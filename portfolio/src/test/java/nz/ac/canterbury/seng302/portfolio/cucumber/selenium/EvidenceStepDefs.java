package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium Cucumber step definitions for the evidence feature.
 */
public class EvidenceStepDefs {

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
     * Navigate to the user search page.
     */
    @And("I browse to the user search page")
    public void iBrowseToTheUserSearchPage() {
        webDriver.findElement(By.id("userSearchButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(., 'Search Users')]")));
    }


    @When("I search for a user {string}")
    public void iSearchForAUser(String userInfo) {
        webDriver.findElement(By.id("searchBar")).sendKeys(userInfo);
    }


    @Then("the user {string} is shown in the search results.")
    public void theUserIsShownInTheSearchResults(String userInfo) {
        assertTrue(webDriver.findElement(By.xpath("//span[contains(., userInfo)]")).isDisplayed());
    }

    @Then("the user {string} is not shown in the search results.")
    public void theUserIsNotShownInTheSearchResults(String username) {
        assertEquals(0, webDriver.findElements(By.xpath("//span[contains(., username)]")).size());
    }
}
