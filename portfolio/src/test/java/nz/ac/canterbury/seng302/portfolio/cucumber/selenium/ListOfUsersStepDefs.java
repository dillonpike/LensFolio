package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.datatable.DataTable;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListOfUsersStepDefs {

    private WebDriver webDriver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        webDriver = SeleniumService.getWebDriver();
        wait = SeleniumService.getWait();
    }

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
}
