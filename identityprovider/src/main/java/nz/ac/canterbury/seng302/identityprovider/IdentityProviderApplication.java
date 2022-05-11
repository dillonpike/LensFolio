package nz.ac.canterbury.seng302.identityprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class IdentityProviderApplication {
    static public String IMAGE_DIR;
    public static void main(String[] args) throws IOException {
        IMAGE_DIR = new File(".").getCanonicalPath() + "/img/";
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

}
