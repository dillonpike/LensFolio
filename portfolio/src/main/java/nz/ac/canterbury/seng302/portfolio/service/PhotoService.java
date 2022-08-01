package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.PortfolioApplication;
import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static nz.ac.canterbury.seng302.portfolio.utility.Utility.getApplicationLocation;

/**
 * Contains methods related to the user's profile photo.
 */
@Service
public class PhotoService {
    @Value("${spring.datasource.url}")
    private String dataSource;

    public String getPhotoPath(String profileImagePath, int userId) {
        String[] paths;
        String path;
        if (profileImagePath.equals("")) {
            return "images/default.jpg";
        } else if (getApplicationLocation(dataSource).equals("dev")) {
            savePhotoToPortfolio(profileImagePath, userId);
            paths = profileImagePath.split("/identityprovider");
            path = paths[1];
        } else {
            paths = profileImagePath.split("/" + getApplicationLocation(dataSource));
            path = "/" + getApplicationLocation(dataSource) + paths[1];
        }

        return path;
    }

    /**
     * Saves the photo from the given path to the portfolio module for loading into the HTML.
     * @param photoPath path of the photo
     */
    public void savePhotoToPortfolio(String photoPath, int userId) {
        try {
            File imageFile;
            String directory = PortfolioApplication.IMAGE_DIR+ "/" + getApplicationLocation(dataSource) + "/" + userId + "/public/";
            new File(directory).mkdirs();
            if (!photoPath.equals("")) {
                String[] paths = photoPath.split("/identityprovider");
                String path = "/portfolio" + paths[1];
                imageFile = new File(photoPath);
                if (imageFile.length() == 0) {
                    imageFile = new File(PortfolioApplication.IMAGE_DIR + "/default.jpg");
                }
            } else {
                imageFile = new File(PortfolioApplication.IMAGE_DIR + "/default.jpg");
            }
            File usedImageFile = new File(directory + "profileImage");
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
