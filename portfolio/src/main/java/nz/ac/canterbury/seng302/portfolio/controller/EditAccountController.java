package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.PortfolioApplication;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.PhotoService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteUserProfilePhotoResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.SizeLimitExceededException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

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
            if(id == userId){
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
            model.addAttribute("dateAdded", Utility.getDateAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("monthsSinceAdded", Utility.getDateSinceAddedString(getUserByIdReply.getCreated()));
            photoService.savePhotoToPortfolio(getUserByIdReply.getProfileImagePath());
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
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
            if(saveUserdata.getIsSuccess()){
                rm.addFlashAttribute("isUpdateSuccess", true);
            } else {
                rm.addFlashAttribute("isUpdateSuccess", false);
            }
        } catch (Exception e) {
            System.err.println("Something went wrong retrieving the data to save");
        }

        rm.addAttribute("userId", userId);

        return "redirect:account";
    }

    @PostMapping("/deleteAccountPhoto")
    public String deletePhoto(
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm,
            Model model
    ) {
        boolean wasDeleted = false;
        String message = "Error occured, caught on portfolio side. ";
        try {
            DeleteUserProfilePhotoResponse reply = registerClientService.deleteUserProfilePhoto(userId);
            wasDeleted = reply.getIsSuccess();
            message = String.valueOf(reply.getMessage());
            if (wasDeleted) {
                new File(PortfolioApplication.IMAGE_DIR).mkdirs();
                File imageFile = new File(PortfolioApplication.IMAGE_DIR + "/default.jpg");
                File usedImageFile = new File(PortfolioApplication.IMAGE_DIR + "/userImage");
                FileOutputStream imageOutput = new FileOutputStream(usedImageFile);
                FileInputStream imageInput = new FileInputStream(imageFile);
                imageOutput.write(imageInput.readAllBytes());
                imageInput.close();
                imageOutput.close();

                rm.addFlashAttribute("isUpdateSuccess", true);
            } else {
                rm.addFlashAttribute("isUpdateSuccess", false);
                rm.addFlashAttribute("message", "Photo failed to delete");
            }

        } catch (Exception e) {
            System.err.println("Something went wrong requesting to delete the photo");
            System.err.println("Message: " + message);
            e.printStackTrace();
        }
        rm.addAttribute("userId", userId);
        return "redirect:editAccount";
    }

    @PostMapping("/saveAccountPhoto")
    public String savePhoto(
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm,
            @RequestParam("avatar") MultipartFile multipartFile,
            Model model
    ) {

        if (multipartFile.isEmpty()) {
            rm.addFlashAttribute("message", "Please select a file to upload.");
            rm.addFlashAttribute("isUpdateSuccess", false);
            return "redirect:editAccount";
        }
        boolean wasSaved = false;
        try {

            new File(PortfolioApplication.IMAGE_DIR).mkdirs();
            File imageFile = new File(PortfolioApplication.IMAGE_DIR + "/userImage");
            FileOutputStream fos = new FileOutputStream( imageFile );
            fos.write( multipartFile.getBytes() );
            fos.close();

            registerClientService.uploadUserProfilePhoto(userId, new File(PortfolioApplication.IMAGE_DIR + "/userImage"));
            // You cant tell if it saves correctly with the above method as it returns nothing
            wasSaved = true;
            if (wasSaved) {
                rm.addFlashAttribute("isUpdateSuccess", true);
            } else {
                rm.addFlashAttribute("isUpdateSuccess", false);
                rm.addFlashAttribute("message", "Photo failed to save");
            }

        } catch (Exception e) {
            System.err.println("Something went wrong requesting to save the photo");
        }
        rm.addAttribute("userId", userId);
        return "redirect:account";
    }


}
