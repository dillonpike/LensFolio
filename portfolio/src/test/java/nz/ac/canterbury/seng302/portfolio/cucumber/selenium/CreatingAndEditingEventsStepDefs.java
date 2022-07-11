package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class CreatingAndEditingEventsStepDefs {

    /**
     * Webdriver used during tests.
     */
    private WebDriver webDriver;

    private String address = "http://localhost:9000/add-event";

    private String expectedName = "Event-Test";

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

    // A better way to do this may involve trying to ensure the <a> link is clickable but because of issues with not being able to click a <a> link with the WebDriver
    // Instead it just ensures that the button exists and the link attached is correct and manuel goes to the page itself.
    @When("I click the add event button")
    public void iClickTheAddEventButton() {
        String link = webDriver.findElement(By.id("addEventButton")).getAttribute("href");
        assertEquals(address, link);
        webDriver.navigate().to(address);
    }

    @Then("the event is created")
    public void theEventIsCreated() {
        boolean found = false;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("widget-49-pro-title")));
        List<WebElement> events = webDriver.findElements(By.className("widget-49-pro-title"));
        for (WebElement event : events) {
            if (Objects.equals(event.getText(), expectedName)) {
                found = true;
            }
        }
        if (!found) {
            fail("No event found.");
        }
    }

    @And("an event exists with the name {string}")
    public void anEventExistsWithTheName(String name) {
    }

    @When("I browse to the edit edit page for an the event named {string}")
    public void iBrowseToTheEditEditPageForAnTheEvent(String name) {

    }

    @And("I change the name to {string}")
    public void iChangeTheNameTo(String name) {

    }

    @Then("a event with the name {string} will exist.")
    public void aEventWithTheNameWillExist(String name) {

    }

    @And("an event exists")
    public void anEventExists() {

    }

    @When("I click delete event.")
    public void iClickDeleteEvent() {

    }

    @Then("the event is deleted from the page.")
    public void theEventIsDeletedFromThePage() {
    }

    @And("I save an event with the name {string}")
    public void iSaveAnEventWithTheName(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("eventName")));
        WebElement nameInput = webDriver.findElement(By.id("eventName"));
        nameInput.sendKeys(name);
        webDriver.findElement(By.id("saveButton")).click();
    }
}
