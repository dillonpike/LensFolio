package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class EditPasswordController {

    @Autowired
    private AuthenticateClientService authenticateClientService;

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountService userAccountService;

    private Utility utility = new Utility();

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
        Integer id = userAccountService.getUserIDFromAuthState(principal);
        try {
            UserResponse getUserByIdReply = registerClientService.getUserData(id);
            String fullName = getUserByIdReply.getFirstName() + " " + getUserByIdReply.getMiddleName() + " " + getUserByIdReply.getLastName();
            model.addAttribute("fullName", fullName);
            model.addAttribute("username", getUserByIdReply.getUsername());
            model.addAttribute("dateAdded", utility.getDateAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("monthsSinceAdded", utility.getDateSinceAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("userId", id);
        } catch(Exception e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
        }
        return "editPassword";
    }

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
        Integer userId = userAccountService.getUserIDFromAuthState(principal);
        System.out.println("user id" + userId);
        try {
            ChangePasswordResponse changeUserPassword = registerClientService.setPassword(userId, currentPassword, newPassword);
            if(changeUserPassword.getIsSuccess()){
                rm.addFlashAttribute("isUpdateSuccess", true);
            } else {
                rm.addFlashAttribute("isUpdateSuccess", false);
            }
        } catch (Exception e) {
            System.err.println("Something went wrong retrieving the data to save");
        }
        rm.addAttribute("userId", userId);

        return "redirect:editAccount";
    }
}
