package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;

@Controller
public class EditPasswordController {

    @Autowired
    private AuthenticateClientService authenticateClientService;

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    /***
     * GET Method
     *
     * Generate the edit password page which let user change password
     *
     * @return The edit password page
     */
    @GetMapping("/editPassword")
    public String showEditPassword(
            Model model,
            HttpServletRequest request,
            @AuthenticationPrincipal AuthState principal
    ) {
        elementService.addUpdateMessage(model, request);
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        try {
            UserResponse getUserByIdReply = registerClientService.getUserData(id);
            ArrayList<String> rolesList = new ArrayList<String>();
            for(int i = 0; i< getUserByIdReply.getRolesCount(); i++){
                String role = getUserByIdReply.getRoles(i).toString();
                if(role == "COURSE_ADMINISTRATOR"){
                    role = "COURSE ADMINISTRATOR";
                }
                rolesList.add(role);
            }
            Collections.sort(rolesList);
            String fullName = getUserByIdReply.getFirstName() + " " + getUserByIdReply.getMiddleName() + " " + getUserByIdReply.getLastName();
            model.addAttribute("fullName", fullName);
            model.addAttribute("username", getUserByIdReply.getUsername());
            model.addAttribute("dateAdded", Utility.getDateAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("monthsSinceAdded", Utility.getDateSinceAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("userId", id);
            model.addAttribute("rolesList", rolesList);
        } catch(Exception e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
        }
        return "editPassword";
    }

    /**
     * Tries to save the new password to the IDP and checks whether it worked or not.
     * @param currentPassword Old password of the user
     * @param newPassword New password of the user
     * @param rm used to get the userId of the user that is having its password changed
     */
    @PostMapping("/savePassword")
    public String savePassword(
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute("currentPassword") String currentPassword,
            @ModelAttribute("newPassword") String newPassword,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes rm,
            Model model
    ) {
        Integer userId = userAccountClientService.getUserIDFromAuthState(principal);
        rm.addAttribute("userId", userId);
        try {
            ChangePasswordResponse changeUserPassword = registerClientService.changePassword(userId, currentPassword, newPassword);
            rm.addFlashAttribute("failureMessage", changeUserPassword.getMessage());
            if (changeUserPassword.getIsSuccess()){
                rm.addFlashAttribute("isUpdateSuccess", true);
                rm.addFlashAttribute("successMessage", changeUserPassword.getMessage());
                return "redirect:editAccount";
            } else {
                rm.addFlashAttribute("isUpdateSuccess", false);
            }
        } catch (Exception e) {
            rm.addFlashAttribute("isUpdateSuccess", false);
            System.err.println("Something went wrong retrieving the data to save");
        }

        return "redirect:editPassword";
    }
}
