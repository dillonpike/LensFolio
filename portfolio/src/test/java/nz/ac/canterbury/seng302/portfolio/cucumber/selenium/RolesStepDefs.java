package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium Cucumber step definitions for the roles feature.
 */
public class RolesStepDefs {

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

    @Then("My account page displays the following roles:")
    public void myAccountPageDisplaysTheFollowingRoles(DataTable rolesDataTable) {
        for (String role: rolesDataTable.asList()) {
            assertTrue(webDriver.findElement(By.xpath("//h6[" + SeleniumService.XPATH_LOWER_CASE_TEXT + "='" +
                    role.toLowerCase() + "']")).isDisplayed());
        }
    }
}
