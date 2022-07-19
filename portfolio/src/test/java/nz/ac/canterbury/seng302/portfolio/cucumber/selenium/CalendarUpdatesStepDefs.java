package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class that tests the GUI flow of the live updates on the calendar.
 */
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
     * Expected Values used for testing.
     */
    private int expectedEventAmount = 0;

    private String expectedDate;

    /**
     * These values are needed because the event default length spans multiple days so
     * They will increase the final event count by more.
     */
    private final int eventDefaultLength = 3;
    private final int extraEvent = 1;

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

    /**
     * Sets up the calendar tab.
     */
    @And("I have the calendar page open on another tab")
    public void iHaveTheCalendarPageOpenOnAnotherTab() {
        ((JavascriptExecutor) webDriver).executeScript("window.open('http://localhost:9000/calendar')");

        tabs = new ArrayList<>(webDriver.getWindowHandles());

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nonExistent")));
        } catch (TimeoutException ignored) {}
        // Switches back to main tab

        webDriver.switchTo().window(tabs.get(0));
    }

    @When("I add an event")
    public void iAddAnEvent() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("projectDropdownButton")));
        webDriver.findElement(By.id("projectDropdownButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addEventButton")));
        webDriver.findElement(By.id("addEventButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventName")));
        webDriver.findElement(By.id("eventName")).sendKeys("Test-Event!");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventModalButton")));
        webDriver.findElement(By.id("eventModalButton")).click();
    }

    /**
     * Counts the number of events present on the page.
     * Compares them to the current expected value.
     */
    @Then("the page is reloaded correctly")
    public void thePageIsReloadedCorrectly() {
        int actualEventAmount = 0;
        for(WebElement el : webDriver.findElements(By.className("fc-event-title"))) {
            try {

                actualEventAmount += Integer.parseInt(el.getText());
            } catch (Exception ignore) {}
        }
        assertEquals(expectedEventAmount + eventDefaultLength + extraEvent, actualEventAmount);
    }

    /**
     * Switch the calendar and get the new month it is on.
     */
    @And("Im not on the default calendar page")
    public void imNotOnTheDefaultCalendarPage() {
        webDriver.switchTo().window(tabs.get(1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title")));
        webDriver.findElement(By.className("fc-next-button")).click();
        expectedDate = webDriver.findElement(By.id("fc-dom-1")).getText();

        webDriver.switchTo().window(tabs.get(0));
    }

    @And("I switch back to the calendar page")
    public void iSwitchBackToTheCalendarPage() {
        webDriver.switchTo().window(tabs.get(1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendar")));
    }

    /**
     * Counts the number of events present on the page.
     */
    @And("I know about the events on the page")
    public void iKnowAboutTheEventsOnThePage() {
        webDriver.switchTo().window(tabs.get(1));
        for(WebElement el : webDriver.findElements(By.className("fc-event-title"))) {
            try {

                expectedEventAmount += Integer.parseInt(el.getText());
            } catch (Exception ignore) {}
        }
        webDriver.switchTo().window(tabs.get(0));
    }

    /**
     * Compares the current page month to the expected.
     */
    @Then("I remain on the calendar same page.")
    public void iRemainOnTheCalendarSamePage() {
        String actualDate = webDriver.findElement(By.id("fc-dom-1")).getText();
        assertEquals(expectedDate, actualDate);
    }
}
