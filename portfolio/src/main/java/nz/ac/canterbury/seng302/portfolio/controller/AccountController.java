package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

@Controller
public class AccountController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountService userAccountService;

    /***
     * Generate the account page which displays all user's info/attributes
     *
     * @return The account(home) page for user
     */
    @GetMapping("/account")
    public String showAccountPage(
            Model model,
            HttpServletRequest request,
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "userId") int userId
    ) {
        Integer id = userAccountService.getLoggedInUserID(request);
        System.out.println("Currently logged in ID: " + id);
        if(id == userId){
            model.addAttribute("isAuthorised", true);
        } else {
            model.addAttribute("isAuthorised", false);
        }

        UserResponse getUserByIdReply;
        UserResponse getUserByIdReplyHeader;
        try {
            getUserByIdReplyHeader =registerClientService.getUserData(id);
            String fullNameHeader = getUserByIdReplyHeader.getFirstName() + " " + getUserByIdReplyHeader.getMiddleName() + " " + getUserByIdReplyHeader.getLastName();
            model.addAttribute("headerFullName", fullNameHeader);

            getUserByIdReply = registerClientService.getUserData(userId);
            model.addAttribute("firstName", getUserByIdReply.getFirstName());
            model.addAttribute("lastName", getUserByIdReply.getLastName());
            model.addAttribute("username", getUserByIdReply.getUsername());
            model.addAttribute("middleName", getUserByIdReply.getMiddleName());
            model.addAttribute("email", getUserByIdReply.getEmail());
            model.addAttribute("personalPronouns", getUserByIdReply.getPersonalPronouns());
            model.addAttribute("bio", getUserByIdReply.getBio());
            String fullName = getUserByIdReply.getFirstName() + " " + getUserByIdReply.getMiddleName() + " " + getUserByIdReply.getLastName();
            model.addAttribute("fullName", fullName);
            model.addAttribute("userId", id);
            model.addAttribute("dateAdded", getDateAddedString(getUserByIdReply.getCreated()));
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
        }

        return "account";
    }

    private String getDateAddedString(Timestamp dateAdded) {
        if (dateAdded != null) {
            Date date = new Date(dateAdded.getSeconds() * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            return dateFormat.format(date);
        } else {
            return null;
        }
    }

    @PostMapping("/backToAccountPage")
    public String editAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm
    ) {
        System.out.println("enter load edit account");
        rm.addAttribute("userId",userId);
        return "redirect:account";
    }
}
