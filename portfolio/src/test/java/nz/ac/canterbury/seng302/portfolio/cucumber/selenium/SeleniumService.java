package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Objects;

/**
 * Contains methods for Selenium tests.
 */
public class SeleniumService {

    /**
     * String that can be included in an xpath to get the text of an element as lowercase.
     */
    static final String XPATH_LOWER_CASE_TEXT = "translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')";

    /**
     * Webdriver used during tests.
     */
    private static WebDriver webDriver;

    /**
     * WebDriverWait object that is used to wait until some criteria is met, for example an element to be visible.
     */
    private static WebDriverWait wait;

    /**
     * Sets up the webDriver and wait objects.
     *
     * WebDriver uses chrome by default, or firefox if the browser system
     * property is set to firefox. This can be set by adding "-Dbrowser=firefox" either as an argument when running
     * ./gradlew test or to the VM options section when running with a Cucumber run configuration.
     *
     * Gives the wait object a 5-second timeout (will throw a timeout exception if it has to wait for 5 seconds).
     */
    private static void setupWebDriver() {
        if (webDriver == null) {
            if (Objects.equals(System.getProperty("browser"), "firefox")) {
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
            } else {
                WebDriverManager.chromedriver().setup();
                webDriver = new ChromeDriver();
            }
        }
        webDriver.manage().window().maximize();
        wait = new WebDriverWait(webDriver, 5);
    }

    /**
     * Returns the webDriver.
     * @return webDriver
     */
    public static WebDriver getWebDriver() {
        setupWebDriver();
        return webDriver;
    }

    /**
     * Returns the WebDriverWait object.
     * @return WebDriverWait object
     */
    public static WebDriverWait getWait() {
        setupWebDriver();
        return wait;
    }

    /**
     * Tears down webDriver by closing the browser, then setting the variable to null.
     */
    public static void tearDownWebDriver() {
        if (webDriver != null) {
            webDriver.quit();
            webDriver = null;
        }
    }
}
