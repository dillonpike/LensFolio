package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.AssertionFailedError;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfilePhotoStepDefs {

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

    @When("I click the delete photo button")
    public void iClickTheDeletePhotoButton() {
        webDriver.findElement(By.id("deletePhotoButton")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("removeUpdateAlert")));
    }

    @Then("My profile photo is the default photo")
    public void myProfilePhotoIsTheDefaultPhoto() {
        assertTrue(webDriver.findElement(By.id("uploadPreview")).isDisplayed());

        WebElement profilePhotoElement = webDriver.findElement(By.id("uploadPreview"));

        BufferedImage expectedImage = null;
        try {
            expectedImage = ImageIO.read(new File("src/main/resources/static/img/default.jpg"));
        } catch (IOException e) {
            fail("Error loading default profile photo during test");
        }
        Screenshot logoImageScreenshot = new AShot().takeScreenshot(webDriver, profilePhotoElement);
        BufferedImage actualImage = logoImageScreenshot.getImage();

        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(actualImage, expectedImage);
        assertTrue(diff.hasDiff(), "Images are Sames");
    }

    @Given("My profile photo is not the default photo")
    public void myProfilePhotoIsNotTheDefaultPhoto() throws InterruptedException {
        try {
            myProfilePhotoIsTheDefaultPhoto();
            iUploadAProfilePhoto();
        } catch (AssertionFailedError e) {
            // Profile photo is not the default photo so step passes
        }
    }

    @When("I upload a profile photo")
    public void iUploadAProfilePhoto() throws InterruptedException {
        webDriver.findElement(By.id("avatar")).sendKeys(new File("src/test/resources/static/img/T100Logo.png").getAbsolutePath());
        wait.until(ExpectedConditions.elementToBeClickable(By.id("save-btn")));
        webDriver.findElement(By.id("save-btn")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("removeUpdateAlert")));
    }

    @Then("My small version of my profile photo is displayed in the header of the page")
    public void mySmallVersionOfMyProfilePhotoIsDisplayedInTheHeaderOfThePage() {
        assertTrue(webDriver.findElement(By.id("userIconSmall")).isDisplayed());
        WebElement profilePhotoElement = webDriver.findElement(By.id("userIconSmall"));
        BufferedImage expectedImage = null;
        try {
            expectedImage = ImageIO.read(new File("src/main/resources/static/img/userImage.jpg"));
        } catch (IOException e) {
            fail("Error loading default profile photo during test");
        }
        Screenshot logoImageScreenshot = new AShot().takeScreenshot(webDriver, profilePhotoElement);
        BufferedImage actualImage = logoImageScreenshot.getImage();

        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(actualImage, expectedImage);
        assertTrue(diff.hasDiff(), "Images are Sames");
    }
}
