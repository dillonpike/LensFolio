package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium Cucumber step definitions for the project details feature.
 */
public class ProjectDetailsStepDefs {

    /**
     * Webdriver used during tests.
     */
    private WebDriver webDriver;

    /**
     * WebDriverWait object that is used to wait until some criteria is met, for example an element to be visible.
     */
    private WebDriverWait wait;

    /**
     * End date of the last sprint.
     */
    private Date lastSprintEndDate;

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

    @Then("I can view a page with details of the project")
    public void i_can_view_a_page_with_details_of_the_project() {
        assertTrue(webDriver.findElement(By.className("title")).isDisplayed());
        assertTrue(webDriver.findElement(By.className("project-desc")).isDisplayed());
        assertTrue(webDriver.findElement(By.className("sprint-block")).isDisplayed());
    }

    @Then("I can create and add all details for a project")
    public void i_can_create_and_add_all_details_for_a_project() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthsAfterNow = now.plusMonths(3);
        String today = dtf.format(now);
        String threeMonthsTime = dtf.format(threeMonthsAfterNow);

        webDriver.findElement(By.className("edit-project-button")).click();
        webDriver.findElement(By.id("projectName")).sendKeys("test project");
        webDriver.findElement(By.id("projectStartDate")).sendKeys(today);
        webDriver.findElement(By.id("projectEndDate")).sendKeys(threeMonthsTime);
        webDriver.findElement(By.id("projectDescription")).sendKeys("test project desc");

        webDriver.findElement(By.id("saveButton")).click();

        //assertTrue(webDriver.findElement(By.className("title")).isDisplayed());
        assertTrue(webDriver.findElement(By.className("project-desc")).getText().equalsIgnoreCase("test project desc"));
        //assertTrue(webDriver.findElement(By.className("sprint-block")).isDisplayed());
    }

    @When("I browse to the project page")
    public void iBrowseToTheProjectPage() {
        webDriver.findElement(By.id("projectsHeaderButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(., 'Project Description')]")));
    }

    @And("I am on the project page")
    public void iAmOnTheProjectPage() {
        iBrowseToTheProjectPage();
    }


    @When("I browse to the add sprint page")
    public void iBrowseToTheAddSprintPage() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("addSprintButton")));
        webDriver.findElement(By.id("addSprintButton")).click();
    }

    @And("There is a sprint")
    public void thereIsASprint() {
        while (!webDriver.findElements(By.id("deleteSprintButton")).isEmpty()) {
            webDriver.findElement(By.id("deleteSprintButton")).click();
        }
        iAddASprint();
        String dateString = webDriver.findElement(By.className("sprint-date")).getText();
        lastSprintEndDate = Project.stringToDate(dateString.substring(dateString.indexOf("-")+2));
    }

    @And("I add a sprint")
    public void iAddASprint() {
        webDriver.findElement(By.id("addSprintButton")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("saveButton")));
        webDriver.findElement(By.id("saveButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(., 'Project Description')]")));
    }

    @Then("The start date should be one day after the end date of the previous sprint")
    public void theStartDateShouldBeOneDayAfterTheEndDateOfThePreviousSprint() {
        String sprintStartDate = webDriver.findElement(By.id("sprintStartDate")).getAttribute("value");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastSprintEndDate);
        calendar.add(Calendar.DATE, 1);
        String expectedStartDate = Project.dateToString(calendar.getTime());
        assertEquals(expectedStartDate, sprintStartDate);
    }

    @And("The end date should be {int} weeks after the start date")
    public void theEndDateShouldBeWeeksAfterTheStartDate(int numWeeks) {
        String sprintStartDate = webDriver.findElement(By.id("sprintStartDate")).getAttribute("value");
        String sprintEndDate = webDriver.findElement(By.id("sprintEndDate")).getAttribute("value");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Project.stringToDate(sprintStartDate));
        calendar.add(Calendar.WEEK_OF_MONTH, 3);
        calendar.add(Calendar.DATE, -1);
        String expectedEndDate = Project.dateToString(calendar.getTime());
        assertEquals(expectedEndDate, sprintEndDate);
    }
}
