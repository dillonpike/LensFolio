package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RolesStepDefs {

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

    @Then("My account page displays the following roles:")
    public void myAccountPageDisplaysTheFollowingRoles(DataTable rolesDataTable) {
        for (String role: rolesDataTable.asList()) {
            assertTrue(webDriver.findElement(By.xpath("//h6[" + SeleniumService.XPATH_LOWER_CASE_TEXT + "='" +
                    role.toLowerCase() + "']")).isDisplayed());
        }
    }
}
