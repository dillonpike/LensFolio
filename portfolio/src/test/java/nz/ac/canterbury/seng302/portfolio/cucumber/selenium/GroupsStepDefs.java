package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;


import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium Cucumber step definitions for the groups feature (U5).
 */
public class GroupsStepDefs {

    /**
     * Webdriver used during tests.
     */
    private WebDriver webDriver;

    /**
     * WebDriverWait object that is used to wait until some criteria is met, for example an element to be visible.
     */
    private WebDriverWait wait;

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

    @Then("I can see group list and group table")
    public void iCanSeeGroupListAndGroupTable() {
        assertTrue(webDriver.findElement(By.id("group_table")).isDisplayed());
        assertTrue(webDriver.findElement(By.id("group_list")).isDisplayed());
    }

    @When("I click on the teacher group")
    public void iClickOnTheTeacherGroup() {
        webDriver.findElement(By.id("groupCard2")).click();
    }

    @When("I click the group setting button")
    public void iClickTheGroupSettingButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("groupSettingButton")));
        webDriver.findElement(By.id("groupSettingButton")).click();
    }

    @Then("I can see the group setting page")
    public void iCanSeeTheGroupSettingPage() {
        assertTrue(webDriver.findElement(By.id("groupSettingTitle")).isDisplayed());
        assertTrue(webDriver.findElement(By.id("repositorySetting")).isDisplayed());
        assertTrue(webDriver.findElement(By.id("groupMember")).isDisplayed());
        assertTrue(webDriver.findElement(By.id("RecentActivity")).isDisplayed());

    }
}
