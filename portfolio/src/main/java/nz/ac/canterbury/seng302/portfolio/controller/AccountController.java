package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalUnit;
import java.util.Map;

@Controller
public class AccountController {

    @Autowired
    private RegisterClientService registerClientService;


    private Utility utility = new Utility();

    /***
     * Generate the account page which displays all user's info/attributes
     *
     * @return The account(home) page for user
     */
    @GetMapping("/account")
    public String showAccountPage(
            Model model,
            @RequestParam(value = "userId") int userId
    ) {

        UserResponse getUserByIdReply;

        try {
            getUserByIdReply = registerClientService.getUserData(userId);
            model.addAttribute("firstName", getUserByIdReply.getFirstName());
            model.addAttribute("lastName", getUserByIdReply.getLastName());
            model.addAttribute("username", getUserByIdReply.getUsername());
            model.addAttribute("middleName", getUserByIdReply.getMiddleName());
            model.addAttribute("nickName", getUserByIdReply.getNickname());
            model.addAttribute("email", getUserByIdReply.getEmail());
            model.addAttribute("personalPronouns", getUserByIdReply.getPersonalPronouns());
            model.addAttribute("bio", getUserByIdReply.getBio());
            System.out.println("this is the bio = " +getUserByIdReply.getBio());
            String fullName = getUserByIdReply.getFirstName() + " " + getUserByIdReply.getMiddleName() + " " + getUserByIdReply.getLastName();
            model.addAttribute("fullName", fullName);
            model.addAttribute("userId", userId);
            model.addAttribute("dateAdded", utility.getDateAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("monthsSinceAdded", utility.getDateSinceAddedString(getUserByIdReply.getCreated()));
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
        }
        return "account";
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
