package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Contains methods related to the user's profile photo.
 */
@Service
public class PhotoService {
    @Value("${spring.datasource.url}")
    private String dataSource;

    public String getPhotoPath(String profileImagePath) {
        if (profileImagePath.equals("")) {
            return "images/default.jpg";
        }
        String[] paths = profileImagePath.split("/" + Utility.getApplicationLocation(dataSource));
        return "/" + Utility.getApplicationLocation(dataSource) + paths[1];
    }
}
