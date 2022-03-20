package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class AccountController {

    @Autowired
    private RegisterClientService registerClientService;

    /***
     * Generate the account page which displays all user's info/attributes
     *
     * @return The account(home) page for user
     */
    @GetMapping("/account")
    public String showAccountPage(
            Model model,
            RedirectAttributes rm
    ) {
        System.out.println("The user id is"+ model.getAttribute("userId"));
        //For testing only
        model.addAttribute("firstName", "Testing");
        model.addAttribute("lastName", "Testing");
        model.addAttribute("nickname", "Testing");
        Map<String, ?> attributes =  rm.getFlashAttributes();
        int userId;
        try {
            userId = (int) model.getAttribute("userId");
        } catch (Exception e) {
            userId = 3;
            System.out.println("error in casting");
            e.printStackTrace();
        }

        UserResponse getUserByIdReply;

        try {
            System.out.println("The user id is "+ userId);
            getUserByIdReply = registerClientService.getUserData(userId);
            System.out.println("this is the user "+ getUserByIdReply.getFirstName());
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            e.printStackTrace();
        }

        // Giving the model the new variables
        //model.addAttribute("firstName", getUserByIdReply.getFirstName());
        //model.addAttribute("lastName", getUserByIdReply.getLastName());
        //model.addAttribute("nickname", getUserByIdReply.getNickname());

        return "account";
    }
}
