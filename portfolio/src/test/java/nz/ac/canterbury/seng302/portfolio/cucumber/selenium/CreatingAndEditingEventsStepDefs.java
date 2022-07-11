package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

public class CreatingAndEditingEventsStepDefs {

    /**
     * Webdriver used during tests.
     */
    private WebDriver webDriver;

    private String address = "http://localhost:9000/add-event";

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

    @When("I click the add event button")
    public void iClickTheAddEventButton() {
        webDriver.findElement(By.xpath("//a[contains(text(),'Add Event')]")).click();
        assertEquals(address, "http://localhost:9000/add-event");
    }

    @Then("the event is created")
    public void theEventIsCreated() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
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
}
