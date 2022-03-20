package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequestOrBuilder;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import nz.ac.canterbury.seng302.portfolio.service.EditAccountClientService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class EditAccountController {

    @Autowired
    private EditAccountClientService editAccountClientService;

    @Autowired
    private RegisterClientService registerClientService;

    /***
     * Generate the edit account page which let user edit info/attributes
     *
     * @return The edit account page
     */
    @GetMapping("/editAccount")
    public String showEditAccountPage(
            @RequestParam(value = "userId") int userId, Model model
    ) {
        UserResponse getUserByIdReply;
        try {
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
            model.addAttribute("userId", userId);
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
