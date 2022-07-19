package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static nz.ac.canterbury.seng302.portfolio.controller.SprintLifetimeController.getUpdatedDate;
import static org.junit.jupiter.api.Assertions.*;

public class LiveUpdatesStepDefs {

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
     * Create mockEvent to be used to define an event if none exist.
     */
    private final Date startEvent = new Date();
    private final Date endEvent = getUpdatedDate(startEvent, 5, 0);
    private final LocalTime startTime = LocalTime.now();
    private final LocalTime endTime = LocalTime.now().plusHours(10L);
    private final String mockEventName = "Test Event";
    private Event mockEvent = new Event(1,0,mockEventName, startEvent, endEvent,startTime, endTime);
    private int eventIdToUse = 1;

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

    @Given("An event exists")
    public void an_event_exists() throws Exception {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("projectsHeaderButton")));
        webDriver.findElement(By.id("projectsHeaderButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        WebElement parent = webDriver.findElement(By.id("event"));
        List<WebElement> listOfEvents = parent.findElements(By.className("card"));
        if (listOfEvents.size() == 0) {
            //wait.until(ExpectedConditions.elementToBeClickable(By.id("addEventButton")));
            //webDriver.findElement(By.id("addEventButton")).click();
            webDriver.get("http://localhost:9000/add-event");
            wait.until(ExpectedConditions.elementToBeClickable(By.id("eventName")));
            webDriver.findElement(By.id("eventName")).sendKeys("Test Event");
            webDriver.findElement(By.id("saveButton")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
            eventIdToUse = Integer.parseInt(webDriver.findElement(By.id("event")).findElement(By.className("card")).getAttribute("value"));
        } else {
            System.out.println("list was not empty");
            System.out.println(parent.findElement(By.className("card")).getAttribute("value") + "< eventId");
            eventIdToUse = Integer.parseInt(parent.findElement(By.className("card")).getAttribute("value"));
        }
    }

    @When("I browse to the edit edit page for an event")
    public void i_browse_to_the_edit_edit_page_for_an_event() {
        webDriver.get("http://localhost:9000/edit-event/" + eventIdToUse);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("eventName")));
    }

    @And("I have the details page open on another tab")
    public void iHaveTheDetailsPageOpenOnAnotherTab() {
        ((JavascriptExecutor) webDriver).executeScript("window.open('http://localhost:9000/details')");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        tabs = new ArrayList<String>(webDriver.getWindowHandles());
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nonExistent")));
        } catch (TimeoutException ignored) {}
        // Switches back to main tab
        webDriver.switchTo().window(tabs.get(0));
    }

    @When("I edit an event")
    public void i_edit_an_event() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("eventName")));
        webDriver.findElement(By.id("eventName")).click();
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nonExistent")));
        } catch (TimeoutException ignored) {}

    }

    @When("I save an event")
    public void i_save_an_event() {
        webDriver.findElement(By.id("saveButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
    }

    @And("I switch tabs back to the details page")
    public void i_switch_tabs_back_to_the_details_page() {
        webDriver.switchTo().window(tabs.get(1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        assertTrue(true);
    }

    @And("I switch tabs back to the event page")
    public void i_switch_tabs_back_to_the_event_page() {
        webDriver.switchTo().window(tabs.get(0));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        assertTrue(true);
    }

    @Then("a live notification appears on the details page with correct message {string}")
    public void a_live_notification_appears_on_the_details_page_with_correct_message(String messageType) {
//        webDriver.switchTo().window(tabs.get(1));
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        String pattern = "";
        if (messageType.equals("Is being edited")) {
            pattern = "^'.*' is being edited by .*$";
        } else if (messageType.equals("Has been saved")) {
            pattern = "^'.*' has been updated by .*$";
        } else {
            fail("Not a valid message to see displayed: '" + messageType + "' ");
        }
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nonExistent")));
        } catch (TimeoutException ignored) {}
        String popupText1 = webDriver.findElement(By.id("popupText1")).getText();
        String popupText2 = webDriver.findElement(By.id("popupText2")).getText();
        String popupText3 = webDriver.findElement(By.id("popupText3")).getText();
        if (!popupText1.equals("")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popupText1")));
            System.out.println(webDriver.findElement(By.id("popupText1")).getText() + "< This");
            assertTrue(webDriver.findElement(By.id("popupText1")).getText().matches(pattern));
            assertTrue(webDriver.findElement(By.id("liveToast1")).isDisplayed());
        }
        if (!popupText2.equals("")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popupText2")));
            System.out.println(webDriver.findElement(By.id("popupText2")).getText() + "< This");
            assertTrue(webDriver.findElement(By.id("popupText2")).getText().matches(pattern));
            assertTrue(webDriver.findElement(By.id("liveToast2")).isDisplayed());
        }
        if (!popupText3.equals("")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popupText3")));
            System.out.println(webDriver.findElement(By.id("popupText3")).getText() + "< This");
            assertTrue(webDriver.findElement(By.id("popupText3")).getText().matches(pattern));
            assertTrue(webDriver.findElement(By.id("liveToast3")).isDisplayed());
        }
    }

    @When("I stop editing an event")
    public void i_stop_editing_an_event() {
        webDriver.switchTo().window(tabs.get(0));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("title")));
        webDriver.findElement(By.id("title")).click();
    }

    @Then("a live notification disappears from the details page after {int} seconds")
    public void a_live_notification_disappears_from_the_details_page_after_seconds(Integer timeInSeconds) {
//        webDriver.switchTo().window(tabs.get(1));
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        WebDriverWait customWait = new WebDriverWait(webDriver, timeInSeconds); // 'timeInSeconds' wait time
        String popupText1 = webDriver.findElement(By.id("popupText1")).getText();
        String popupText2 = webDriver.findElement(By.id("popupText2")).getText();
        String popupText3 = webDriver.findElement(By.id("popupText3")).getText();
        if (!popupText1.equals("")) {
            customWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("liveToast1")));
            assertFalse(webDriver.findElement(By.id("liveToast1")).isDisplayed());
        }
        else if (!popupText2.equals("")) {
            customWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("liveToast2")));
            assertFalse(webDriver.findElement(By.id("liveToast2")).isDisplayed());
        }
        else if (!popupText3.equals("")) {
            customWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("liveToast3")));
            assertFalse(webDriver.findElement(By.id("liveToast3")).isDisplayed());
        }
    }



    @And("I open edit milestone modal")
    public void iOpenEditMilestoneModal() {
        webDriver.findElement(By.id("milestone-tab")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("milestoneActions")));
        webDriver.findElement(By.id("milestoneActions")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editMilestoneButton")));
        webDriver.findElement(By.id("editMilestoneButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[contains(., 'Edit Milestone')]")));
    }

    @When("I edit an milestone")
    public void iEditAnMilestone() {
        webDriver.findElement(By.id("milestoneName")).click();
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nonExistent")));
        } catch (TimeoutException ignored) {}
    }

    @And("I save the update")
    public void iSaveTheUpdate() {
        webDriver.findElement(By.id("milestoneModalButton")).click();
    }

    @And("I stop editing an milestone")
    public void iStopEditingAnMilestone() {
        webDriver.findElement(By.id("exampleModalLongTitle")).click();
    }
}
