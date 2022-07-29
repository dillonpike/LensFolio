package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.PortfolioApplication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Contains methods related to the user's profile photo.
 */
@Service
public class PhotoService {

    public String getPhotoPath(String profileImagePath) {
        if (profileImagePath.equals("")) {
            return "images/default.jpg";
        }
        return profileImagePath;
    }
}
