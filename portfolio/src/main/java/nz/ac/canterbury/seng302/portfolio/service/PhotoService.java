package nz.ac.canterbury.seng302.portfolio.service;

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
            new File("src/main/resources/static/img").mkdirs();
            System.out.println(photoPath + "<-- Photo path from IDP");
            if (!photoPath.equals("")) {
                imageFile = new File(photoPath);
                System.out.println(imageFile.length() + "<-- File length of file in filepath");
                if (imageFile.length() == 0) {
                    imageFile = new File("src/main/resources/static/img/default.jpg");
                }
            } else {
                imageFile = new File("src/main/resources/static/img/default.jpg");
            }
            System.out.println(imageFile.getAbsolutePath() + "<-- Absolute file path chosen");
            File usedImageFile = new File("src/main/resources/static/img/userImage");
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
