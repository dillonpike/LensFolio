package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
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
            model.addAttribute("nickName", getUserByIdReply.getNickname());
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
            RedirectAttributes rm,
            Model model
    ) {
        System.out.println("enter load edit account"+userId);
        rm.addAttribute("userId", userId);
        return "redirect:editAccount";
    }

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
        System.out.println(userId);
        System.out.println(email);
        System.out.println(personalPronouns);
        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(middleName);
        System.out.println(bio);
//        try {
//            EditUserResponse saveUserdata = registerClientService.setUserData(id, firstName, middleName, lastName, email, bio, nickName, personalPronouns);
//        } catch (Exception e) {
//            System.err.println("Something went wrong retrieving the data to save");
//            e.printStackTrace();
//        }
//
//        rm.addAttribute("userId", id);
        return "redirect:editAccount";
    }
}
