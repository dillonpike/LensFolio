package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
            Model model
    ) {
        //For testing only
        model.addAttribute("firstName", "Testing");
        model.addAttribute("lastName", "Testing");
        model.addAttribute("nickname", "Testing");
        int userId;
        try {
            userId = (int) model.getAttribute("userId");
        } catch (Exception e) {
            userId = 2;
        }

        UserResponse getUserByIdReply;

        try {
            getUserByIdReply = registerClientService.getUserData(userId);
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
