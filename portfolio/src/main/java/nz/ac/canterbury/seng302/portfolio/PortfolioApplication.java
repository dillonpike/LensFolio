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
    public static String IMAGE_DIR;
    public static void main(String[] args) throws IOException {
        // Sets up the pathway being used by the dynamic images.
        IMAGE_DIR = new File(".").getCanonicalPath();
        SpringApplication.run(PortfolioApplication.class, args);
    }
}
