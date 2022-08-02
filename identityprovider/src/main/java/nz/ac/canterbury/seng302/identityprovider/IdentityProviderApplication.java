package nz.ac.canterbury.seng302.identityprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class IdentityProviderApplication {

    /**
     * Pathway being used to store user images.
     */
    static public String IMAGE_DIR;
    public static void main(String[] args) throws IOException {
        // Sets up the pathway for the user images.
        IMAGE_DIR = new File(".").getCanonicalPath() + "/";
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

}
