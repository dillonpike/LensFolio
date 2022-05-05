package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GeneralSeleniumSteps {

    private static WebDriver webDriver;
    private static WebDriverWait wait;

    @Before
    public void setUp() {
        webDriver = SeleniumService.getWebDriver();
        wait = SeleniumService.getWait();
    }

    @After
    public void tearDown() {
        SeleniumService.tearDownWebDriver();
    }

    @When("I log in as admin")
    public void iLogInAsAdmin() {
        webDriver.navigate().to("http://localhost:9000/login");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
        webDriver.findElement(By.id("usernameLogin")).sendKeys("admin");
        webDriver.findElement(By.id("passwordLogin")).sendKeys("password");
        webDriver.findElement(By.id("signIn")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h2[contains(., 'Profile')]")));
    }

    @Given("I am logged in as admin")
    public void iAmLoggedInAsAdmin() {
        iLogInAsAdmin();
    }

    @And("I log out")
    public void iLogOut() {
        webDriver.findElement(By.id("dropdownUser1")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("signOutButton")));
        webDriver.findElement(By.id("signOutButton")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("usernameLogin")));
    }

    @When("I browse to the account page")
    public void iBrowseToTheAccountPage() {
        webDriver.findElement(By.id("dropdownUser1")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("profileButton")));
        webDriver.findElement(By.id("profileButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(., 'Profile')]")));
    }

    @And("I am on the edit account page")
    public void iAmOnTheEditAccountPage() {
        iBrowseToTheAccountPage();
        webDriver.findElement(By.id("editProfileButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(., 'Edit Profile')]")));
    }
}
