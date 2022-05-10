package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.PhotoService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***
 * Controller receives HTTP GET, POST, PUT, DELETE calls for account page
 */
@Controller
public class AccountController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    @Autowired
    private PhotoService photoService;

    /***
     * GET method for account controller to generate user's info
     *
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param userIdInput ID for the current login user
     * @return Account page which including user's info
     */
    @GetMapping("/account")
    public String showAccountPage(
            Model model,
            HttpServletRequest request,
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "userId") String userIdInput
    ) {
        UserResponse getUserByIdReply;
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        elementService.addUpdateMessage(model, request);
        try {
            int userId = Integer.parseInt(userIdInput);
            if(id == userId){
                model.addAttribute("isAuthorised", true);
            } else {
                model.addAttribute("isAuthorised", false);
            }
            getUserByIdReply = registerClientService.getUserData(userId);
            if (getUserByIdReply.getEmail().length() == 0) {
                model.addAttribute("userId", id);
                return "404NotFound";
            }
            elementService.addRoles(model, getUserByIdReply);
            model.addAttribute("firstName", getUserByIdReply.getFirstName());
            model.addAttribute("lastName", getUserByIdReply.getLastName());
            model.addAttribute("username", getUserByIdReply.getUsername());
            model.addAttribute("middleName", getUserByIdReply.getMiddleName());
            model.addAttribute("nickName", getUserByIdReply.getNickname());
            model.addAttribute("email", getUserByIdReply.getEmail());
            model.addAttribute("personalPronouns", getUserByIdReply.getPersonalPronouns());
            model.addAttribute("bio", getUserByIdReply.getBio());
            String fullName = getUserByIdReply.getFirstName() + " " + getUserByIdReply.getMiddleName() + " " + getUserByIdReply.getLastName();
            model.addAttribute("fullName", fullName);
            model.addAttribute("userId", id);
            model.addAttribute("dateAdded", Utility.getDateAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("monthsSinceAdded", Utility.getDateSinceAddedString(getUserByIdReply.getCreated()));
            String portfolioAbsolutePath = photoService.savePhotoToPortfolio(getUserByIdReply.getProfileImagePath());
            model.addAttribute("uploadPreview", portfolioAbsolutePath);
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
        } catch (NumberFormatException numberFormatException) {
            model.addAttribute("userId", id);
            return "404NotFound";
        }

        return "account";
    }


    /***
     * Handler for HTTP POST request, followed by (../backToAccountPage)
     * We load the current userID from HTML file which contains current userID,
     * redirect to account page
     *
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param userId userId ID for the current login user
     * @param rm attributes pass to other controller
     * @return Account page with user id
     */
    @PostMapping("/backToAccountPage")
    public String editAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm
    ) {
        rm.addAttribute("userId",userId);
        return "redirect:account";
    }


}
