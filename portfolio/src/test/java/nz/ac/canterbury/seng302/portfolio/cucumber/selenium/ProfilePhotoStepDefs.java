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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium Cucumber step definitions for the profile photo feature.
 */
public class ProfilePhotoStepDefs {

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

    @When("I click the delete photo button")
    public void iClickTheDeletePhotoButton() {
        webDriver.findElement(By.id("deletePhotoButton")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("removeUpdateAlert")));
    }

    /**
     * Returns true if the user's profile photo is the same as the image stored at imagePath, otherwise false.
     * @param profilePhotoElement WebElement that displays the user's profile photo
     * @param imagePath image to check the user's profile photo is the same as
     * @return true if the profile photo is the same, otherwise false
     */
    private boolean profilePhotoIsSameAsImageFromPath(WebElement profilePhotoElement, String imagePath) {
        BufferedImage expectedImage = null;
        try {
            expectedImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            fail("Error loading expected image during test");
        }
        Screenshot logoImageScreenshot = new AShot().takeScreenshot(webDriver, profilePhotoElement);
        BufferedImage actualImage = logoImageScreenshot.getImage();

        File actualTest = new File("src/test/resources/static/img/actualTest.jpg");
        File expectedTest = new File("src/test/resources/static/img/expectedTest.jpg");

        BufferedImage resizedImage = new BufferedImage(actualImage.getWidth(), actualImage.getHeight(), BufferedImage.SCALE_SMOOTH);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(expectedImage, 0, 0, actualImage.getWidth(), actualImage.getHeight(), null);
        graphics2D.dispose();


        try {
            boolean check1 = ImageIO.write(actualImage, "png", actualTest);
            boolean check2 = ImageIO.write(resizedImage, "png", expectedTest);
            System.err.println("Written" + check1 + check2);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(actualImage, resizedImage).withDiffSizeTrigger((int) (actualImage.getHeight() * actualImage.getWidth() * 0.5));

        System.err.println(diff.hasDiff());
        System.err.println(actualImage.getHeight() * actualImage.getWidth());
        return !diff.hasDiff();
    }

    @Then("My profile photo is the default photo")
    public void myProfilePhotoIsTheDefaultPhoto() {
        assertTrue(webDriver.findElement(By.id("uploadPreview")).isDisplayed());
        assertTrue(profilePhotoIsSameAsImageFromPath(webDriver.findElement(By.id("uploadPreview")),
                "src/main/resources/static/img/default.jpg"));
    }

    @Given("My profile photo is not the default photo")
    public void myProfilePhotoIsNotTheDefaultPhoto() {
//        try {
////            myProfilePhotoIsTheDefaultPhoto();
//            iUploadAProfilePhoto();
//        } catch (AssertionFailedError e) {
//            // Profile photo is not the default photo so step passes
//        }
        if (profilePhotoIsSameAsImageFromPath(webDriver.findElement(By.id("uploadPreview")),
                "src/main/resources/static/img/default.jpg")) {
            iUploadAProfilePhoto();
        }
    }

    @When("I upload a profile photo")
    public void iUploadAProfilePhoto() {
        webDriver.findElement(By.id("avatar")).sendKeys(new File("src/test/resources/static/img/T100Logo.jpg").getAbsolutePath());
        wait.until(ExpectedConditions.elementToBeClickable(By.id("save-btn")));
        webDriver.findElement(By.id("save-btn")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("removeUpdateAlert")));
        assertTrue(profilePhotoIsSameAsImageFromPath(webDriver.findElement(By.id("uploadPreview")),
                "src/test/resources/static/img/T100Logo.jpg"));
    }

    @Then("My small version of my profile photo is displayed in the header of the page")
    public void mySmallVersionOfMyProfilePhotoIsDisplayedInTheHeaderOfThePage() {
        assertTrue(webDriver.findElement(By.id("userIconSmall")).isDisplayed());
        assertTrue(profilePhotoIsSameAsImageFromPath(webDriver.findElement(By.id("userIconSmall")),
                "src/main/resources/static/img/userImage.jpg"));
    }
}
