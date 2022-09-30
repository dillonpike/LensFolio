package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.controller.DetailsController;
import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.utility.ToastUtility;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.hibernate.ObjectNotFoundException;
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

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private UserSortingService userSortingService;

    private static final String UPDATE_STATE_ATTRIBUTE_NAME = "isUpdateSuccess";

    private static final String UPDATE_MESSAGE_ATTRIBUTE_NAME = "updateMessage";

    private static final String FAILURE_MESSAGE_ATTRIBUTE_NAME = "failureMessage";

    /**
     * Updates the given model with an updateMessage attribute.
     *
     * If isUpdateSuccess in the request is true, updateMessage will be set to successMessage from the request, or a
     * default success message if successMessage doesn't exist. If isUpdateSuccess is false, updateMessage will be set
     * to failureMessage from the request, or a default failure message if failureMessage doesn't exist.
     *
     * @param model model from controller method that attributes will be added to
     * @param request HTTP requests from controller method
     */
    public void addUpdateMessage(Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            boolean isUpdateSuccess = (boolean) inputFlashMap.get(UPDATE_STATE_ATTRIBUTE_NAME);
            if (isUpdateSuccess) {
                model.addAttribute(UPDATE_STATE_ATTRIBUTE_NAME, true);
                String message = inputFlashMap.containsKey("successMessage") ?
                        (String) inputFlashMap.get(FAILURE_MESSAGE_ATTRIBUTE_NAME) : "Account Information Successfully Updated";
                model.addAttribute(UPDATE_MESSAGE_ATTRIBUTE_NAME, message);
            } else {
                model.addAttribute(UPDATE_STATE_ATTRIBUTE_NAME, false);
                String message = inputFlashMap.containsKey(FAILURE_MESSAGE_ATTRIBUTE_NAME) ?
                        (String) inputFlashMap.get(FAILURE_MESSAGE_ATTRIBUTE_NAME) : "Update Canceled! Something went wrong!";
                model.addAttribute(UPDATE_MESSAGE_ATTRIBUTE_NAME, message);
            }
        }
    }

    @Value("${spring.datasource.url}")
    private String dataSource;

    /**
     * Update the given model with a 'access denied' Message attribute
     * @param model model from controller method that attributes will be added to
     * @param request HTTP requests from controller method
     */
    public void addDeniedMessage(Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            boolean isUpdateSuccess = (boolean) inputFlashMap.get("isAccessDenied");
            if (isUpdateSuccess) {
                model.addAttribute(UPDATE_STATE_ATTRIBUTE_NAME, true);
                model.addAttribute(UPDATE_MESSAGE_ATTRIBUTE_NAME, "Access Denied, Please log out and try again");
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
        // Gets the dynamic image spring is hosting for that user or the default image.
        model.addAttribute("userHeaderImage", photoService.getPhotoPath(userData.getProfileImagePath(), userId));
        model.addAttribute("userId", userId);
        ToastUtility.addToastsToModel(model, new ArrayList<>(), DetailsController.NUM_OF_TOASTS);
    }

    /**
     * Updates the given model with the user's roles.
     * @param model model from controller method that attributes will be added to
     * @param userData UserResponse object of the currently signed on user
     */
    public void addRoles(Model model, UserResponse userData) {
        ArrayList<String> rolesList = new ArrayList<>();
        for (int i = 0; i< userData.getRolesCount(); i++) {
            String role = userData.getRoles(i).toString();
            rolesList.add(role.replace("_", " "));
        }
        Collections.sort(rolesList);
        model.addAttribute("rolesList", rolesList);
    }

    /**
     * Method to return current user's highest role in string format.
     *
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

    /**
     * Adds all users in the application, as well as the currently logged-in user's saved sorting, to the model.
     * @param model model from controller method that attributes will be added to
     * @param id id of currently logged-in user
     */
    public void addUsersToModel(Model model, Integer id) {
        PaginatedUsersResponse response = userAccountClientService.getAllUsers();
        List<UserResponse> userResponseList = response.getUsersList();
        model.addAttribute("users", userResponseList);
        UserSorting userSorting;
        try {
            userSorting = userSortingService.getUserSortingById(id);
        } catch (ObjectNotFoundException e) {
            userSorting = new UserSorting(id);
        }
        model.addAttribute("userSorting", userSorting);
    }
}
