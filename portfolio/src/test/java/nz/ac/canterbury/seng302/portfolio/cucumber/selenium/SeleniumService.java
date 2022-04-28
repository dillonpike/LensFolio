package nz.ac.canterbury.seng302.portfolio.cucumber.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Objects;

public class SeleniumService {

    private static WebDriver webDriver;
    private static WebDriverWait wait;

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
        wait = new WebDriverWait(webDriver, 5);
    }

    public static WebDriver getDriver() {
        setupWebDriver();
        return webDriver;
    }

    public static WebDriverWait getWait() {
        setupWebDriver();
        return wait;
    }

    public static void tearDownWebDriver() {
        if (webDriver != null) {
            webDriver.quit();
            webDriver = null;
        }
    }
}
