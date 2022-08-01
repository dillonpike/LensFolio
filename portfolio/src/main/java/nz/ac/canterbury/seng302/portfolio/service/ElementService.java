package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Contains methods that add attributes to the page's model, so they can be displayed on the page.
 */
@Service
public class ElementService {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private PhotoService photoService;

    /**
     * Updates the given model with an updateMessage attribute.
     *
     * If isUpdateSuccess in the request is true, updateMessage will be set to successMessage from the request, or a
     * default success message if successMessage doesn't exist. If isUpdateSuccess is false, updateMessage will be set
     * to failureMessage from the request, or a default failure message if failureMessage doesn't exist.
     *
     * @param model model from controller method that attributes will be added to
     * @param request HTTP request from controller method
     */
    public void addUpdateMessage(Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            boolean isUpdateSuccess = (boolean) inputFlashMap.get("isUpdateSuccess");
            if (isUpdateSuccess) {
                model.addAttribute("isUpdateSuccess", true);
                String message = inputFlashMap.containsKey("successMessage") ?
                        (String) inputFlashMap.get("failureMessage") : "Account Information Successfully Updated";
                model.addAttribute("updateMessage", message);
            } else {
                model.addAttribute("isUpdateSuccess", false);
                String message = inputFlashMap.containsKey("failureMessage") ?
                        (String) inputFlashMap.get("failureMessage") : "Update Canceled! Something went wrong!";
                model.addAttribute("updateMessage", message);
            }
        }
    }

    @Value("${spring.datasource.url}")
    private String dataSource;

    /**
     * Updates the given model with the user's full name for display in the header.
     * @param model model from controller method that attributes will be added to
     * @param userId id of the currently signed on user
     */
    public void addHeaderAttributes(Model model, int userId) {
        UserResponse userData = registerClientService.getUserData(userId);
        String fullNameHeader = userData.getFirstName() + " " + userData.getMiddleName() + " " + userData.getLastName();
        model.addAttribute("headerFullName", fullNameHeader);
        // Gets the dynamic image spring is hosting for that user or the default image.
        model.addAttribute("userImage", photoService.getPhotoPath(userData.getProfileImagePath(), userId));
    }

    /**
     * Updates the given model with the user's roles.
     * @param model model from controller method that attributes will be added to
     * @param userData UserResponse object of the currently signed on user
     */
    public void addRoles(Model model, UserResponse userData) {
        ArrayList<String> rolesList = new ArrayList<String>();
        for (int i = 0; i< userData.getRolesCount(); i++) {
            String role = userData.getRoles(i).toString();
            rolesList.add(role.replace("_", " "));
        }
        Collections.sort(rolesList);
        model.addAttribute("rolesList", rolesList);
    }
}
