package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.PortfolioApplication;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.PhotoService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.portfolio.utility.DateUtility;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteUserProfilePhotoResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.MessageFormat;

import static nz.ac.canterbury.seng302.portfolio.utility.Utility.getApplicationLocation;

/***
 * Controller receive HTTP GET, POST, PUT, DELETE calls for edit account page
 */
@Controller

public class EditAccountController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    @Autowired
    private PhotoService photoService;

    @Value("${spring.datasource.url}")
    private String dataSource;

    private static final Logger logger = LoggerFactory.getLogger(EditAccountController.class);

    /***
     * GET method to generate the edit account page which let user edit info/attributes
     * @param userIdInput ID for the current user
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param request HTTP request sent to this endpoint
     * @return the edit account page which let user edit info/attributes
     */
    @GetMapping("/editAccount")
    public String showEditAccountPage(
            Model model,
            HttpServletRequest request,
            @RequestParam(value = "userId") String userIdInput,
            @AuthenticationPrincipal AuthState principal
    ) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        UserResponse getUserByIdReply;
        elementService.addUpdateMessage(model, request);
        try {
            int userId = Integer.parseInt(userIdInput);
            if (id == userId) {
                model.addAttribute("isAuthorised", true);
            } else {
                model.addAttribute("isAuthorised", false);
            }
            getUserByIdReply = registerClientService.getUserData(id);
            elementService.addRoles(model, getUserByIdReply);
            model.addAttribute("firstName", getUserByIdReply.getFirstName());
            model.addAttribute("nickName", getUserByIdReply.getNickname());
            model.addAttribute("lastName", getUserByIdReply.getLastName());
            model.addAttribute("username", getUserByIdReply.getUsername());
            model.addAttribute("middleName", getUserByIdReply.getMiddleName());
            model.addAttribute("email", getUserByIdReply.getEmail());
            model.addAttribute("personalPronouns", getUserByIdReply.getPersonalPronouns());
            model.addAttribute("bio", getUserByIdReply.getBio());
            String fullName = getUserByIdReply.getFirstName() + " " + getUserByIdReply.getMiddleName() + " " + getUserByIdReply.getLastName();
            model.addAttribute("fullName", fullName);
            model.addAttribute("userId", id);
            model.addAttribute("dateAdded", DateUtility.getDateAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("monthsSinceAdded", DateUtility.getDateSinceAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("userImage", photoService.getPhotoPath(getUserByIdReply.getProfileImagePath(), userId));
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            logger.error(MessageFormat.format(
                    "Error connecting to Identity Provider: {0}", e.getMessage()));
        } catch (NumberFormatException numberFormatException) {
            model.addAttribute("userId", id);
            return "404NotFound";
        }
       return "editAccount";
    }

    /***
     * POST Method
     *
     * This process works in a few stages:
     *  1. We send Post request "editAccountLoad" when user click edit profile
     *  2. We Load the current user's id and add it to model
     *  3. Redirect to account page use GET Method
     *
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param userId ID for the current user
     * @param rm attributes pass to other controller
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Account Page
     */
    @PostMapping("/editAccountLoad")
    public String editAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm,
            Model model
    ) {
        rm.addAttribute("userId", userId);
        return "redirect:editAccount";
    }

    /***
     * POST Method
     *
     * Post the changed user made in the edit account page, check the response,
     * and if it is successful new attributes will be stored for future use.
     *
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param userId UserId of the current login user
     * @param email New email associated with username
     * @param firstName New firstName associated with username
     * @param lastName New lastName associated with username
     * @param middleName New middleName associated with username
     * @param nickName New nickName associated with username
     * @param personalPronouns New personalPronouns associated with username
     * @param bio New bio associated with username
     * @param rm Redirect attributes
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return redirect back to account page
     */
    @PostMapping("/saveEditAccount")
    public String saveEditAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute("userId") int userId,
            @ModelAttribute("email") String email,
            @ModelAttribute("firstName") String firstName,
            @ModelAttribute("lastName") String lastName,
            @ModelAttribute("middleName") String middleName,
            @ModelAttribute("nickName") String nickName,
            @ModelAttribute("personalPronouns") String personalPronouns,
            @ModelAttribute("bio") String bio,
            RedirectAttributes rm,
            Model model
    ) {
        try {
            EditUserResponse saveUserdata = registerClientService.setUserData(userId, firstName, middleName, lastName, email, bio, nickName, personalPronouns);
            if (saveUserdata.getIsSuccess()) {
                rm.addFlashAttribute("isUpdateSuccess", true);
            } else {
                rm.addFlashAttribute("isUpdateSuccess", false);
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format(
                    "Something went wrong retrieving the data to save: {0}", e.getMessage()));
        }

        rm.addAttribute("userId", userId);

        return "redirect:account";
    }

    /**
     * Post method used to remove the users profile image. This is simply done through a request to the IDP
     * The image itself is not deleted but its connection to the user is (deleting its path).
     * This may need to be changed in the future if private images are implemented.
     *
     * @param userId    The user's ID.
     * @param rm        The attributes being sent back.
     * @param model     The modal being used by Thymeleaf
     * @return The redirected edit account page.
     */
    @PostMapping("/deleteAccountPhoto")
    public String deletePhoto(
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm,
            Model model
    ) {

        String message = "Photo failed to delete.";
        String messageID = "message";
        String updateCheckID = "isUpdateSuccess";

        try {
            DeleteUserProfilePhotoResponse reply = registerClientService.deleteUserProfilePhoto(userId);
            message = reply.getMessage();

            if (reply.getIsSuccess()) { // Updated successful
                rm.addFlashAttribute(updateCheckID, true);
            } else { // Updated not successful
                rm.addFlashAttribute(updateCheckID, false);
                rm.addFlashAttribute(messageID, message);
            }
        } catch (Exception ignore) { // Updated error
            rm.addFlashAttribute(updateCheckID, false);
            rm.addFlashAttribute(messageID, message);
        }
        rm.addAttribute("userId", userId);

        return "redirect:editAccount";
    }



    /**
     * Post method used to get the multipart file that is being uploaded.
     * It will then save the file locally and send it to the IDP through bidirectional streaming (that is not done here)
     *
     * @param userId        The user's ID.
     * @param rm            The attributes being sent back.
     * @param multipartFile File being uploaded.
     * @param model         The modal being used by Thymeleaf
     * @return The redirected edit account page.
     */
    @PostMapping("/saveAccountPhoto")
    public String savePhoto(
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm,
            @RequestParam("avatar") MultipartFile multipartFile,
            Model model
    ) {
        String messageID = "message";
        String updateCheckID = "isUpdateSuccess";

        if (multipartFile.isEmpty()) { // Checks that the file isn't empty.
            rm.addFlashAttribute(messageID, "Please select a file to upload.");
            rm.addFlashAttribute(updateCheckID, false);
            return "redirect:editAccount";
        }

        try { // Makes image folder structure and then saves multipart image which is then streamed to the IDP.
            String directory = MessageFormat.format("{0}/{1}/{2}/",
                    PortfolioApplication.IMAGE_DIR, getApplicationLocation(dataSource), userId);
            String filePath = directory + "/UploadedFile";

            if (!new File(directory).mkdirs()) { // Ensures folders are made.
                logger.warn("Not all folders may have been created.");
            }

            File imageFile = new File(filePath); // Saves image locally so the file can be streamed to the IDP.
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(multipartFile.getBytes());
            fos.close();

            if (registerClientService.uploadUserProfilePhoto(userId, new File(filePath))) { // Saves image on IDP.
                rm.addFlashAttribute(updateCheckID, true);
                rm.addFlashAttribute("reloadImage", true);
            } else {
                rm.addFlashAttribute(updateCheckID, false);
                rm.addFlashAttribute(messageID, "Photo failed to save");
            }
        } catch (Exception e) { // Error in saving locally.
            logger.error(MessageFormat.format(
                    "Something went wrong requesting to save the photo: {0}", e.getMessage()));
        }

        rm.addAttribute("userId", userId);
        return "redirect:editAccount";
    }

    /**
     * Reloads the page and makes the update message appear.
     * @param rm Redirect attributes
     * @return Thymeleaf template
     */
    @GetMapping("/reloadAccountSuccessfulPage")
    public String reloadPage(
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm
    ) {
        rm.addAttribute("userId", userId);
        rm.addFlashAttribute("isUpdateSuccess", true);
        return "redirect:editAccount";
    }


}
