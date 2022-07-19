package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;

public class CalendarUpdatesStepDefs {

    /**
     * Webdriver used during tests.
     */
    private WebDriver webDriver;

    /**
     * WebDriverWait object that is used to wait until some criteria is met, for example an element to be visible.
     */
    private WebDriverWait wait;

    /**
     * List of tabs currently open.
     */
    private ArrayList<String> tabs;

    /**
     * Sets up for scenario by getting a web driver.
     */
    @Before
    public void setUp() {
        webDriver = SeleniumService.getWebDriver();
        wait = SeleniumService.getWait();
        wait.withTimeout(Duration.ofSeconds(1));
    }

    /**
     * Tears down after running scenario by quitting the web driver (thus closing the browser) and setting the web
     * driver to null.
     */
    @After
    public void tearDown() {
        SeleniumService.tearDownWebDriver();
    }

    @And("I have the calendar page open on another tab")
    public void iHaveTheCalendarPageOpenOnAnotherTab() {
        ((JavascriptExecutor) webDriver).executeScript("window.open('http://localhost:9000/calendar')");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        tabs = new ArrayList<String>(webDriver.getWindowHandles());
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nonExistent")));
        } catch (TimeoutException ignored) {}
        // Switches back to main tab
        webDriver.switchTo().window(tabs.get(0));
    }

    @When("I add an event")
    public void iAddAnEvent() {

    }

    @Then("the page is reloaded correctly")
    public void thePageIsReloadedCorrectly() {

    }

    @When("I remove an event")
    public void iRemoveAnEvent() {

    }

    @And("Im not on the default calendar page")
    public void imNotOnTheDefaultCalendarPage() {

    }

    @Then("I remain on the same page.")
    public void iRemainOnTheSamePage() {
    }
}
