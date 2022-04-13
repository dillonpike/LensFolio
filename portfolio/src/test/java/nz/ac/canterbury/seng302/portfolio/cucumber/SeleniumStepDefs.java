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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeleniumStepDefs {
    private WebDriver webDriver;
    private final int delay = 5;
    WebDriverWait wait;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver",
                Paths.get("src/test/resources/chromedriver_win32/chromedriver.exe").toString());
        if (webDriver == null) {
            webDriver = new ChromeDriver();
        }
        wait = new WebDriverWait(webDriver, delay);
    }

    @After
    public void tearDown() {
        if (webDriver != null) {
            webDriver.close();
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
}
