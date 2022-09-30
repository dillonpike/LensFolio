package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium Cucumber step definitions for the evidence deletion feature.
 */
public class EvidenceDeletionStepDefs {

    /**
     * Webdriver used during tests.
     */
    private WebDriver webDriver;

    /**
     * WebDriverWait object that is used to wait until some criteria is met, for example an element to be visible.
     */
    private WebDriverWait wait;

    /**
     * Evidence name of the piece of evidence to be deleted (Or being manipulated).
     */
    private String evidenceName;

    /**
     * Sets up for scenario by getting a web driver and WebDriverWait object.
     */
    @Before
    public void setUp() {
        webDriver = SeleniumService.getWebDriver();
        wait = SeleniumService.getWait();
        wait.withTimeout(Duration.ofSeconds(3));
    }

    /**
     * Navigate to the user's evidence page/tab.
     */
    @And("I am on the evidence tab")
    public void iAmOnTheEvidenceTab() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("evidence-tab")));
        webDriver.findElement(By.id("evidence-tab")).click();
    }

    /**
     * Find if the user has a piece of evidence. If not, then add one.
     */
    @And("I have added a piece of evidence")
    public void iHaveAddedAPieceOfEvidence() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("evidenceDropdown1")));
        } catch (Exception e) {
            webDriver.findElement(By.id("addEvidenceButton")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("evidenceTitle")));
            webDriver.findElement(By.id("evidenceTitle")).sendKeys("Test Evidence");
            webDriver.findElement(By.id("evidenceDescription")).sendKeys("Test Description");
            webDriver.findElement(By.id("evidenceModalButton")).click();
        }
    }

    /**
     * Click on the delete button for the piece of evidence.
     */
    @When("I click on the delete icon")
    public void iClickOnTheDeleteIcon() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("evidenceActions")));
        webDriver.findElement(By.id("evidenceActions")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteEvidenceDisplayButton")));
        webDriver.findElement(By.id("deleteEvidenceDisplayButton")).click();
        evidenceName = webDriver.findElement(By.className("evidence-title")).getText();
    }

    /**
     * The delete evidence modal is showing.
     */
    @Then("a delete evidence prompt is presented")
    public void aDeleteEvidencePromptIsPresented() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteModal")));
    }

    /**
     * Checks that the title of the evidence is shown.
     */
    @Then("the title of the piece of evidence should appear on the prompt")
    public void theTitleOfThePieceOfEvidenceShouldAppearOnThePrompt() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteModal")));
        assertTrue(webDriver.findElement(By.id("deleteModal")).findElement(By.tagName("label")).getText().contains(evidenceName));
    }

    @When("I click away from the delete evidence prompt")
    public void iClickAwayFromTheDeleteEvidencePrompt() {
        new Actions(webDriver).moveToElement(webDriver.findElement(By.id("uploadPreview"))).click().perform();
    }

    @When("I click the x button on the delete evidence prompt")
    public void iClickTheXButtonOnTheDeleteEvidencePrompt() {
        webDriver.findElement(By.id("deleteModalCloseButton")).click();
    }

    @Then("the delete evidence prompt is closed")
    public void theDeleteEvidencePromptIsClosed() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("deleteModal")));
    }
}
