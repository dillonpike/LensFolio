package nz.ac.canterbury.seng302.portfolio.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;

/**
 * This test the photo service logic. This currently entails testing the paths returned by its getPhotoPath() method.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class PhotoServiceTest {

    /**
     * This is spied so that the savePhotoToPortfolio() method can be ignored.
     */
    @Spy
    PhotoService photoService;

    /**
     * This is necessary to be passed to the method but doesn't affect the paths returned.
     */
    private final int userID = 1;


    /**
     * This tests ensures that images without a path return the default user image path.
     * The current system (dev, test and prod) don't affect this test.
     */
    @Test
    void WhenGivenEmptyImagePath_ExpectDefaultImagePath() {
        String imagePath = "";
        String expectedPath = "images/default.jpg";
        String actualPath = photoService.getPhotoPath(imagePath, userID);
        assertEquals(expectedPath, actualPath);
    }

    /**
     * This tests ensures that images with a valid path return the correctly formatted user image path.
     * Because this is testing with the prod system it folder is shared with the IDP.
     * Thus, the original image path is cut off from "/prod."
     */
    @Test
    void WhenGivenNonEmptyImagePath_AndNonDevDataSource_ExpectRealImagePath() {
        String prodSource = "jdbc:mariadb://db2.csse.canterbury.ac.nz/seng302-2022-team100-portfolio-prod";
        photoService.setDataSource(prodSource);
        String imagePath = "useless/paths/prod/1/public/userImage.png";
        String expectedPath = "prod/1/public/userImage.png";
        String actualPath = photoService.getPhotoPath(imagePath, userID);
        assertEquals(expectedPath, actualPath);
    }

    /**
     * This tests ensures that images with a valid path return the correctly formatted user image path.
     * Because this is testing with the dev system it folder is not shared with the IDP.
     * Thus, the original image path is cut off from "/identityprovider."
     */
    @Test
    void WhenGivenNonEmptyImagePath_AndDevDataSource_ExpectRealImagePath() {
        String prodSource = "jdbc:mariadb://localhost:3306/jth141_portfolio-test";
        photoService.setDataSource(prodSource);
        doNothing().when(photoService).savePhotoToPortfolio(anyString(), anyInt());
        String imagePath = "useless/paths/identityprovider/prod/1/public/userImage.png";
        String expectedPath = "/prod/1/public/userImage.png";
        String actualPath = photoService.getPhotoPath(imagePath, userID);
        assertEquals(expectedPath, actualPath);
    }

    /**
     * This tests ensures that images with a valid path return the correctly formatted user image path.
     * Because this is testing with the dev system it folder is not shared with the IDP.
     * Thus, the original image path is cut off from "/identityprovider."
     * <p>
     * This test also check to ensure that as local system may have different image pathways formatting styles,
     * that they are changed and made consistent with what the other image system methods expect.
     */
    @Test
    void WhenGivenNonEmptyImagePathWithBackSlashes_AndDevDataSource_ExpectRealImagePath() {
        String prodSource = "jdbc:mariadb://localhost:3306/jth141_portfolio-test";
        photoService.setDataSource(prodSource);
        doNothing().when(photoService).savePhotoToPortfolio(anyString(), anyInt());
        String imagePath = "useless\\paths\\identityprovider\\prod\\1\\public\\userImage.png";
        String expectedPath = "/prod/1/public/userImage.png";
        String actualPath = photoService.getPhotoPath(imagePath, userID);
        assertEquals(expectedPath, actualPath);
    }


}