package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void addDeniedMessage(Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            boolean isUpdateSuccess = (boolean) inputFlashMap.get("isAccessDenied");
            if (isUpdateSuccess) {
                model.addAttribute("isUpdateSuccess", true);
                model.addAttribute("updateMessage", "Access Denied, Please log out and try again");
            }
        }
    }

    /**
     * Updates the given model with the user's full name for display in the header.
     * @param model model from controller method that attributes will be added to
     * @param userId id of the currently signed on user
     */
    public void addHeaderAttributes(Model model, int userId) {
        UserResponse userData = registerClientService.getUserData(userId);
        String fullNameHeader = userData.getFirstName() + " " + userData.getMiddleName() + " " + userData.getLastName();
        model.addAttribute("headerFullName", fullNameHeader);
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

    /**
     * Set the current user's permission based on user's role by adding role attribute to the model
     * @param userResponse UserResponse object of the currently signed on user
     */
    public String getUserHighestRole(UserResponse userResponse) {
        List<Integer> roleList = userResponse.getRolesValueList();
        //Check if user is a course administrator. Otherwise, check current user is a teacher
        if (!roleList.contains(2)) {
            if (!roleList.contains(1)) {
                //User must have one role, therefore set user permission to student
                return "student";
            } else {
                //If roleList contains 1(teacher role), set user permission to teacher
                return "teacher";
            }
        } else {
            //If roleList contains 2(admin role), set user permission to admin
            return "admin";
        }
    }
}
