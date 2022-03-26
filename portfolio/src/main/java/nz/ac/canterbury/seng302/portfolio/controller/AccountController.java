package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AccountController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountService userAccountService;


    private Utility utility = new Utility();

    /***
     * GET method for account controller to generate user's info
     *
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
//     * @param userId ID for the current login user
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
        UserResponse getUserByIdReplyHeader;
        try {
            int userId = Integer.parseInt(userIdInput);
            Integer id = userAccountService.getUserIDFromAuthState(principal);
            System.out.println("Currently logged in ID: " + id);
            if(id == userId){
                model.addAttribute("isAuthorised", true);
            } else {
                model.addAttribute("isAuthorised", false);
            }
            getUserByIdReplyHeader = registerClientService.getUserData(id);
            String fullNameHeader = getUserByIdReplyHeader.getFirstName() + " " + getUserByIdReplyHeader.getMiddleName() + " " + getUserByIdReplyHeader.getLastName();
            model.addAttribute("headerFullName", fullNameHeader);

            getUserByIdReply = registerClientService.getUserData(userId);
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
            model.addAttribute("dateAdded", utility.getDateAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("monthsSinceAdded", utility.getDateSinceAddedString(getUserByIdReply.getCreated()));
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
        } catch (NumberFormatException numberFormatException) {
            //TODO Show error message. Invalid URL
        }

        return "account";
    }


    /***
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
