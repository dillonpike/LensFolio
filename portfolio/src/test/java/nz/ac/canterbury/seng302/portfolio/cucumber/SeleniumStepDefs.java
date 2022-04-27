package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumStepDefs {
    private WebDriver webDriver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        if (Objects.equals(System.getProperty("browser"), "firefox")) {
            WebDriverManager.firefoxdriver().setup();
            webDriver = new FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            webDriver = new ChromeDriver();
        }
        wait = new WebDriverWait(webDriver, 5);
    }

    @After
    public void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    @Given("I log in as admin")
    public void iLogInAsAdmin() {
        webDriver.navigate().to("http://localhost:9000/login");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
        webDriver.findElement(By.id("usernameLogin")).sendKeys("admin");
        webDriver.findElement(By.id("passwordLogin")).sendKeys("password");
        webDriver.findElement(By.id("signIn")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h2[contains(., 'Profile')]")));
    }

    @And("I browse to the list of users page")
    public void iBrowseToTheListOfUsersPage() {
        webDriver.findElement(By.id("usersHeaderButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(., 'Participants')]")));
    }

    @When("I sort the list of users by username descending")
    public void iSortTheListOfUsersByUsernameDescending() {
        WebElement usernameColumn = webDriver.findElement(By.id("usernameColumn"));
        while (usernameColumn.getAttribute("aria-sort") == null || !usernameColumn.getAttribute("aria-sort").equals("descending")) {
            usernameColumn.click();
        }
    }

    @And("I log out")
    public void iLogOut() {
        webDriver.findElement(By.id("profileImageHeader")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("signOutButton")));
        webDriver.findElement(By.id("signOutButton")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
    }

    @Then("The list of users is sorted by username descending")
    public void theListOfUsersIsSortedByUsernameDescending() {
        assertEquals("descending", webDriver.findElement(By.id("usernameColumn")).getAttribute("aria-sort"));
    }

    @Given("I am logged in as admin")
    public void iAmLoggedInAsAdmin() {
        iLogInAsAdmin();
    }

    @And("I am on the list of users page")
    public void iAmOnTheListOfUsersPage() {
        iBrowseToTheListOfUsersPage();
    }

    @Then("I can see a list of users")
    public void iCanSeeAListOfUsers() {
        assertTrue(webDriver.findElement(By.id("sortTable")).isDisplayed());
        // Check table contains at least one row
        assertTrue(webDriver.findElement(By.xpath("//tbody/tr")).isDisplayed());
    }
}
