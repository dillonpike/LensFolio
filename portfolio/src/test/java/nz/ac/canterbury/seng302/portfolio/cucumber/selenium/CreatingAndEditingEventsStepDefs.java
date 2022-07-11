package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
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

    private final String address = "http://localhost:9000/add-event";

    private final String expectedName = "Event-Test";

    private int actualValue;

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
    public void ensureEventIsRemoved() {
        SeleniumService.tearDownWebDriver();
    }
    /**
     * Tears down after running scenario by quitting the web driver (thus closing the browser) and setting the web
     * driver to null.
     *
     * But also firstly ensures that all events created are deleted before doing so.
     * Only runs when tagged with "EventsNew"
     */
    @After("@EventsNew")
    public void removeEvents() {
        setUp();

        webDriver.navigate().to("http://localhost:9000/login");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
        webDriver.findElement(By.id("usernameLogin")).sendKeys("admin");
        webDriver.findElement(By.id("passwordLogin")).sendKeys("password");
        webDriver.findElement(By.id("signIn")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(., 'Profile')]")));

        webDriver.navigate().to("http://localhost:9000/details");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        List<WebElement> events = webDriver.findElements(By.className("widget-49-pro-title"));
        for (WebElement event : events) {
            if (Objects.equals(event.getText(), expectedName)) {
                int index = events.indexOf(event);
                List<WebElement> deleteEvents = webDriver.findElements(By.className("event-del-button-link"));
                String link = deleteEvents.get(index).getAttribute("href");
                webDriver.navigate().to(link);
            }
        }

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

    @Then("a event with the name {string} will exist.")
    public void aEventWithTheNameGivenWillExist(String name) {
        boolean found = false;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("widget-49-pro-title")));
        List<WebElement> events = webDriver.findElements(By.className("widget-49-pro-title"));
        for (WebElement event : events) {
            if (Objects.equals(event.getText(), name)) {
                found = true;
            }
        }
        if (!found) {
            fail("No event found.");
        }
    }

    @And("an event exists with the name {string}")
    public void anEventExistsWithTheName(String name) {
        webDriver.navigate().to("http://localhost:9000/details");
        iClickTheAddEventButton();
        iSaveAnEventWithTheName(name);
    }

    @When("I browse to the edit edit page for an the event named {string}")
    public void iBrowseToTheEditEditPageForAnTheEvent(String name) {
        boolean found = false;
        List<WebElement> events = webDriver.findElements(By.className("widget-49-pro-title"));
        for (WebElement event : events) {
            if (Objects.equals(event.getText(), name)) {
                found = true;
                int index = events.indexOf(event);
                List<WebElement> deleteEvents = webDriver.findElements(By.className("event-edit-button-link"));
                String link = deleteEvents.get(index).getAttribute("href");
                webDriver.navigate().to(link);
            }
        }
        if (!found) {
            fail("Event not created.");
        }
    }

    @And("I change the name to {string}")
    public void iChangeTheNameTo(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("eventName")));
        WebElement nameInput = webDriver.findElement(By.id("eventName"));
        nameInput.clear();
        nameInput.sendKeys(name);
    }


    @When("I click delete for the event {string}")
    public void iClickDeleteForTheEvent(String name) {
        boolean found = false;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        List<WebElement> events = webDriver.findElements(By.className("widget-49-pro-title"));
        for (WebElement event : events) {
            if (Objects.equals(event.getText(), name)) {
                found = true;
                int index = events.indexOf(event);
                List<WebElement> deleteEvents = webDriver.findElements(By.className("event-del-button-link"));
                String link = deleteEvents.get(index).getAttribute("href");
                webDriver.navigate().to(link);
            }
        }

        if (!found) {
            fail("No event could be found.");
        }
    }

    @Then("the event {string} is deleted from the page.")
    public void theEventIsDeletedFromThePage(String name) {
        boolean found = false;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("widget-49-pro-title")));
        List<WebElement> events = webDriver.findElements(By.className("widget-49-pro-title"));
        for (WebElement event : events) {
            if (Objects.equals(event.getText(), name)) {
                found = true;
            }
        }
        if (found) {
            fail("Event was not deleted from the page.");
        }
    }

    @And("I save an event with the name {string}")
    public void iSaveAnEventWithTheName(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("eventName")));
        WebElement nameInput = webDriver.findElement(By.id("eventName"));
        nameInput.sendKeys(name);
        webDriver.findElement(By.id("saveButton")).click();
    }

    @And("I am on the add event page")
    public void iAmOnTheAddEventPage() {
        webDriver.navigate().to(address);
    }

    @When("I type {string} into the events name")
    public void iTypeIntoTheEventsName(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("eventName")));
        WebElement nameInput = webDriver.findElement(By.id("eventName"));
        nameInput.sendKeys(name);
    }

    @Then("I will be told I have only {int} characters left.")
    public void iWillBeToldIHaveOnlyCharactersLeft(int expectedValue) {
        assertTrue(webDriver.findElement(By.id("eventNameLength")).isDisplayed());

        try {
            String[] strings = webDriver.findElement(By.id("eventNameLength")).getText().split(" ");
            actualValue = Integer.parseInt(strings[0]);
        } catch (Exception e) {
            fail(e);
        }
        assertEquals(expectedValue, actualValue);
    }
}
