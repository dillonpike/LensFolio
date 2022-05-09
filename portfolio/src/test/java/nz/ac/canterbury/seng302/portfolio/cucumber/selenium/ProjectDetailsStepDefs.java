package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectDetailsStepDefs {

    private WebDriver webDriver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        webDriver = SeleniumService.getWebDriver();
        wait = SeleniumService.getWait();
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
    

    @After
    public void tearDown() {
        SeleniumService.tearDownWebDriver();
    }

}
