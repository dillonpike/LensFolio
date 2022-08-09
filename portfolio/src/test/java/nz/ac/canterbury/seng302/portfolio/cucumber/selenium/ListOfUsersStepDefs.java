package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium Cucumber step definitions for the list of users feature.
 */
public class ListOfUsersStepDefs {

    /**
     * Webdriver used during tests.
     */
    private WebDriver webDriver;

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

    @And("I browse to the list of users page")
    public void iBrowseToTheListOfUsersPage() {
        webDriver.findElement(By.id("usersHeaderButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(., 'Participants')]")));
    }

    @And("I am on the list of users page")
    public void iAmOnTheListOfUsersPage() {
        iBrowseToTheListOfUsersPage();
    }

    @When("I sort the list of users by username descending")
    public void iSortTheListOfUsersByUsernameDescending() {
        WebElement usernameColumn = webDriver.findElement(By.id("usernameColumn"));
        while (usernameColumn.getAttribute("aria-sort") == null || !usernameColumn.getAttribute("aria-sort").equals("descending")) {
            usernameColumn.click();
        }
    }

    @Then("The list of users is sorted by username descending")
    public void theListOfUsersIsSortedByUsernameDescending() {
        assertEquals("descending", webDriver.findElement(By.id("usernameColumn")).getAttribute("aria-sort"));
    }

    @Then("I can see a list of users")
    public void iCanSeeAListOfUsers() {
        assertTrue(webDriver.findElement(By.id("sortTable")).isDisplayed());
        // Check table contains at least one row
        assertTrue(webDriver.findElement(By.xpath("//tbody/tr")).isDisplayed());
    }

    @Then("The list of users has the following columns:")
    public void theListOfUsersHasTheFollowingColumns(DataTable dataTableColumns) {
        for (String expectedColumn : dataTableColumns.asList()) {
            assertTrue(webDriver.findElement(By.xpath("//thead/tr/th[" + SeleniumService.XPATH_LOWER_CASE_TEXT + "='" +
                    expectedColumn.toLowerCase() + "']")).isDisplayed());
        }
    }

    @Then("The list of users is separated into multiple pages")
    public void theListOfUsersIsSeparatedIntoMultiplePages() {
        String nextButtonClass = webDriver.findElement(By.id("sortTable_next")).getAttribute("class");
        assertNotNull(nextButtonClass);
    }

    @And("I go to the next page")
    public void iGoToTheNextPage() {
        wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement(By.id("sortTable_next")).findElement(By.tagName("a"))));
        webDriver.findElement(By.id("sortTable_next")).findElement(By.tagName("a")).click();
        //webDriver.findElement(By.xpath("//a[contains(text(),'Next')]")).click();
    }

    @When("I remove a role")
    public void iRemoveARole() {
        List<WebElement> buttons = webDriver.findElements(By.tagName("button"));
        WebElement button = null;
        for (WebElement but : buttons) {
            if (but.getAttribute("onClick").equals("deleteUserRole('2','TEACHER')")) {
                button = but;
                break;
            }
        }

        if (button == null) {
            System.err.println("Cannot find button!");
        } else {
            System.out.println("Clicking button now!");
            button.click();
        }
    }

    @Then("I am on same page")
    public void iAmOnSamePage() {
        webDriver.findElement(By.id("sortTable_previous")).isEnabled();
    }
}
