package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class EditAccountController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private ElementService elementService;

    private Utility utility = new Utility();

    /***
     * GET method to generate the edit account page which let user edit info/attributes
     * @param userId ID for the current user
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param request HTTP request sent to this endpoint
     * @return the edit account page which let user edit info/attributes
     */
    @GetMapping("/editAccount")
    public String showEditAccountPage(
            Model model,
            HttpServletRequest request,
            @RequestParam(value = "userId") int userId,
            @AuthenticationPrincipal AuthState principal
    ) {
        Integer id = userAccountService.getUserIDFromAuthState(principal);
        if(id == userId){
            model.addAttribute("isAuthorised", true);
        } else {
            model.addAttribute("isAuthorised", false);
        }
        UserResponse getUserByIdReply;
        model = elementService.addBanner(model, request);
        try {
            getUserByIdReply = registerClientService.getUserData(id);
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
            model.addAttribute("dateAdded", utility.getDateAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("monthsSinceAdded", utility.getDateSinceAddedString(getUserByIdReply.getCreated()));
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
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

        return "redirect:editAccount";
    }
}
