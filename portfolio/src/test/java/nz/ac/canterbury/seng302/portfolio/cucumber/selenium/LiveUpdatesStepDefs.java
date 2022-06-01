package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.controller.AccountController;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static nz.ac.canterbury.seng302.portfolio.controller.SprintLifetimeController.getUpdatedDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

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


    @MockBean
    private EventService eventService = Mockito.mock(EventService.class);

    @MockBean
    private EventRepository eventRepository = Mockito.mock(EventRepository.class);

    /**
     * Create mockEvent to be returned when a function return an Event object
     */
    private Date startEvent = new Date();
    private Date endEvent = getUpdatedDate(startEvent, 5, 0);
    private LocalTime startTime = LocalTime.now();
    private LocalTime endTime = LocalTime.now().plusHours(10L);
    private String mockEventName = "Test Event";
    private Event mockEvent = new Event(1,0,mockEventName, startEvent, endEvent,startTime, endTime);

    /**
     * Sets up for scenario by getting a web driver.
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

    @Given("event {int} exists")
    public void event_exists(Integer eventId) throws Exception {
        try {
            mockEvent = eventService.getEventById(eventId);
        } catch (Exception e) {
            mockEvent.setId(eventId);
            eventService.addEvent(mockEvent);
        }
    }

    @When("I browse to the edit edit page for event {int}")
    public void i_browse_to_the_edit_edit_page_for_event(Integer eventId) {
        webDriver.get("http://localhost:9000/edit-event/" + eventId);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("eventName")));
    }

    @And("I have the details page open on another tab")
    public void iHaveTheDetailsPageOpenOnAnotherTab() {
        ((JavascriptExecutor) webDriver).executeScript("window.open('http://localhost:9000/details')");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        tabs = new ArrayList<String>(webDriver.getWindowHandles());
        // Switches back to main tab
        webDriver.switchTo().window(tabs.get(0));
    }

    @When("I edit an event {int}")
    public void i_edit_an_event(Integer eventId) {
        webDriver.findElement(By.id("eventName")).click();
    }

    @When("I save an event {int}")
    public void i_save_an_event(Integer eventId) {
        webDriver.findElement(By.id("saveButton")).click();
    }

    @Then("a live notification appears on the details page with correct message {string}")
    public void a_live_notification_appears_on_the_details_page_with_correct_message(String messageType) {
        webDriver.switchTo().window(tabs.get(1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        String pattern = "";
        if (messageType.equals("Is being edited")) {
            pattern = "^'.*' is being edited by .*$";
        } else if (messageType.equals("Has been saved")) {
            pattern = "^'.*' has been updated by .*$";
        } else {
            fail("Not a valid message to see displayed: '" + messageType + "' ");
        }
        assertTrue(webDriver.findElement(By.id("popupText")).getText().matches(pattern));
        assertTrue(webDriver.findElement(By.id("liveToast")).isDisplayed());

    }

    @When("I stop editing an event {int}")
    public void i_stop_editing_an_event(Integer int1) {
        webDriver.findElement(By.id("title")).click();
    }

    @Then("a live notification disappears from the details page after {int} seconds")
    public void a_live_notification_disappears_from_the_details_page_after_seconds(Integer timeInSeconds) {
        webDriver.switchTo().window(tabs.get(1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-name")));
        WebDriverWait customWait = new WebDriverWait(webDriver, timeInSeconds); // 'timeInSeconds' wait time
        customWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("liveToast")));
        assertFalse(webDriver.findElement(By.id("liveToast")).isDisplayed());
    }

}
