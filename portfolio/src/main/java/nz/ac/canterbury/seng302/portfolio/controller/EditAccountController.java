package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
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

@Controller
public class EditAccountController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountService userAccountService;

    /***
     * Generate the edit account page which let user edit info/attributes
     *
     * @return The edit account page
     */
    @GetMapping("/editAccount")
    public String showEditAccountPage(
            @RequestParam(value = "userId") int userId,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            HttpServletRequest request
    ) {
        Integer id = userAccountService.getUserIDFromAuthState(principal);
        if(id == userId){
            model.addAttribute("isAuthorised", true);
        } else {
            model.addAttribute("isAuthorised", false);
        }
        UserResponse getUserByIdReply;
        try {
            getUserByIdReply = registerClientService.getUserData(id);
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
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
        }
       return "editAccount";
    }

    @PostMapping("/editAccountLoad")
    public String editAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm
    ) {
        System.out.println("enter load edit account");
        rm.addAttribute("userId",userId);
        return "redirect:editAccount";
    }
}
