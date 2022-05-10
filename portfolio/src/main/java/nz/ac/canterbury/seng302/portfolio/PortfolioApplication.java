package nz.ac.canterbury.seng302.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class PortfolioApplication {
    static String IMAGE_DIR;
    public static void main(String[] args) throws IOException {
        IMAGE_DIR = new File(".").getCanonicalPath() + "/src/main/resources/static/img/";
        SpringApplication.run(PortfolioApplication.class, args);
    }

}
