package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class RegisterController {

    @Autowired
    private RegisterClientService registerClientService;

    /***
     *  GET method to return registration web page
     * @return the registration page(registration.html)
     */
    @GetMapping("/register")
    public String registration() {
        return "registration";
    }

    /***
     * POST method to send attributes values to Register Client Service to check validation.
     *
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param firstName First name of the new account
     * @param middleName Middle name of the new account
     * @param lastName Last name of the new account
     * @param username Username of the new account
     * @param email Email associated with username
     * @param password Password associated with username
     * @param confirmPassword Confirm password
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return to Account page if user successfully register. Error message sent to user if failed
     */
    @PostMapping("/register")
    public String registration(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "middleName") String middleName,
            @RequestParam(name = "lastName") String lastName,
            @RequestParam(name = "username") String username,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            Model model
    ) {
        UserRegisterResponse registrationReply;

        if (!password.equals(confirmPassword)) {
            return "redirect:register?passwordError";
        }
        try {
            registrationReply = registerClientService.receiveConformation(username, password, firstName, middleName, lastName, email);
        } catch (Exception e) {
            model.addAttribute("err", "Error connecting to Identity Provider...");
            return "login";
        }
        if (registrationReply.getIsSuccess()) {
            return "redirect:register?successfulRegister";
        } else {
            model.addAttribute("err", "Something went wrong");
            return "redirect:register?registerError";
        }
    }
}
