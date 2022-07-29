package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.PortfolioApplication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Contains methods related to the user's profile photo.
 */
@Service
public class PhotoService {

    /**
     * Saves the photo from the given path to the portfolio module for loading into the HTML.
     * @param photoPath path of the photo
     */
    public void savePhotoToPortfolio(String photoPath) {
        try {
            File imageFile;
            new File(PortfolioApplication.IMAGE_DIR).mkdirs();
            if (!photoPath.equals("")) {
                imageFile = new File(photoPath);
                if (imageFile.length() == 0) {
                    imageFile = new File(PortfolioApplication.IMAGE_DIR + "/img/default.jpg");
                }
            } else {
                imageFile = new File(PortfolioApplication.IMAGE_DIR + "/img/default.jpg");
            }
            File usedImageFile = new File(PortfolioApplication.IMAGE_DIR + "/userImage");
            FileOutputStream imageOutput = new FileOutputStream(usedImageFile);
            FileInputStream imageInput = new FileInputStream(imageFile);
            imageOutput.write(imageInput.readAllBytes());
            imageInput.close();
            imageOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
