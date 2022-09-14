package nz.ac.canterbury.seng302.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class PortfolioApplication {

    /**
     * Pathway being used to store dynamic images. This can be used by other controllers.
     */
    private static String imageDir;

    /**
     * Runs the portfolio and sets the image path.
     * @param args none currently used
     * @throws IOException when an error occurs when getting the image path
     */
    public static void main(String[] args) throws IOException {
        // Sets up the pathway being used by the dynamic images.
        imageDir = new File(".").getCanonicalPath();
        SpringApplication.run(PortfolioApplication.class, args);
    }

    /**
     * Returns the pathway used to store dynamic images.
     * @return pathway used to store dynamic images
     */
    public static String getImageDir() {
        return imageDir;
    }
}
