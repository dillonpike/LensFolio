package nz.ac.canterbury.seng302.portfolio.service;


import com.google.common.annotations.VisibleForTesting;
import nz.ac.canterbury.seng302.portfolio.PortfolioApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

import static nz.ac.canterbury.seng302.portfolio.utility.Utility.getApplicationLocation;

/**
 * Contains methods related to the user's profile photo.
 */
@Service
public class PhotoService {
    @Value("${spring.datasource.url}")
    private String dataSource;

    private static final Logger logger = LoggerFactory.getLogger(PhotoService.class);

    /**
     * Gets the users photo path based off the provided image name through profileImagePath and user through userId.
     * Depending on the system running (dev,test,prod) different paths may be returned.
     *
     * @param profileImagePath  IDP file pathway.
     * @param userId            The users ID.
     * @return The path to use to display this image.
     */
    public String getPhotoPath(String profileImagePath, int userId) {
        String[] paths;
        String path;

        if (profileImagePath.equals("")) { // Default image.
            return "images/default.jpg";
        } else if (getApplicationLocation(dataSource).equals("dev")) { // Save image locally as dev is running.
            savePhotoToPortfolio(profileImagePath, userId);
            String trueImagePath;
            // On a dev system the slash's may not be consistent, this ensures that they are.
            trueImagePath = profileImagePath.replaceAll("\\\\", "/");
            paths = trueImagePath.split("/identityprovider");
            path = paths[1];
        } else { // The VM uses a shared folder currently so no additional saving is necessary.
            paths = profileImagePath.split("/" + getApplicationLocation(dataSource));
            path = "/" + getApplicationLocation(dataSource) + paths[1];
        }
        return path;
    }

    /**
     * Saves the photo from the given path to the portfolio module for loading into the HTML.
     * This only occurs on the Dev system as on the VM the folder is accessible from both modals.
     *
     * @param photoPath Path of the photo
     * @param userId    The user's ID.
     */
    public void savePhotoToPortfolio(String photoPath, int userId) {
        try {
            File imageFile;
            String directory = MessageFormat.format("{0}/{1}/{2}/public/",
                    PortfolioApplication.IMAGE_DIR, getApplicationLocation(dataSource), userId);

            if (!new File(directory).mkdirs()) { // Ensures folders are made.
                logger.warn("Not all folders may have been created.");
            }

            imageFile = new File(photoPath); // The photo in the IDP
            if (imageFile.length() == 0) { // If no photo exists use the default image.
                URL resource = getClass().getClassLoader().getResource("static/images/default.jpg");
                if (resource == null) {
                    throw new NullPointerException("No default image.");
                }
                imageFile = new File(resource.toURI());
            }

            File usedImageFile = new File(directory + "profileImage"); // Saves locally for use in HTML.
            FileOutputStream imageOutput = new FileOutputStream(usedImageFile);
            FileInputStream imageInput = new FileInputStream(imageFile);
            imageOutput.write(imageInput.readAllBytes());
            imageInput.close();
            imageOutput.close();
        } catch (IOException | URISyntaxException | NullPointerException e) {
            logger.error(MessageFormat.format("Error with saving image locally: {0}", e.getMessage()));
        }
    }

    public void setDataSource(String newSource) {
        dataSource = newSource;
    }
}
