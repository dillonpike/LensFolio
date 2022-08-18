package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***
 * Controller receive HTTP GET, POST, PUT, DELETE calls for register page
 */
@Controller
public class RegisterController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private AuthenticateClientService authenticateClientService;

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',.?/*~$^+=<>]).{8,20}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);


    String defaultUsername = "defaultUsername";
    String defaultFirstName = "defaultFirstName";
    String defaultMiddleName = "defaultMiddleName";
    String defaultLastName = "defaultLastName";
    String defaultEmail = "defaultEmail";
    String defaultBio = "defaultBio";
    String defaultNickName = "defaultNickName";
    String defaultPronouns = "defaultPronouns";
    /***
     *  GET method to return registration web page
     * @return the registration page(registration.html)
     */
    @GetMapping("/register")
    public String registration(
            @RequestParam(value = "defaultFirstName", required = false) String firstName,
            @RequestParam(value = "defaultMiddleName", required = false) String middleName,
            @RequestParam(value = "defaultLastName", required = false) String lastName,
            @RequestParam(value = "defaultEmail", required = false) String email,
            @RequestParam(value = "defaultUsername", required = false) String username,
            @RequestParam(value = "defaultNickName", required = false) String nickName,
            @RequestParam(value = "defaultPronouns", required = false) String pronouns,
            @RequestParam(value = "defaultBio", required = false) String bio,
            Model model) {

        if (pronouns == null || pronouns.isEmpty()) {
            pronouns = "Unknown Pronouns";
        }

        model.addAttribute("defaultFirstName", firstName);
        model.addAttribute("defaultMiddleName", middleName);
        model.addAttribute("defaultLastName", lastName);
        model.addAttribute("defaultEmail", email);
        model.addAttribute("defaultUsername", username);
        model.addAttribute("defaultPronouns", pronouns);
        model.addAttribute("defaultNickName", nickName);
        model.addAttribute("defaultBio", bio);

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
            @RequestParam(name = "bio") String bio,
            @RequestParam(name = "personalPronouns") String personalPronouns,
            @RequestParam(name = "nickName") String nickName,
            Model model,
            RedirectAttributes rm

    ) {
        AuthenticateResponse loginReply;
        UserRegisterResponse registrationReply;
        String userBio = bio;
        String userNickName = nickName;
        if (!isValid(password)) {
            addModelAttribute(rm, username, firstName, middleName, lastName, email);
            rm.addAttribute(defaultNickName, nickName);
            rm.addAttribute(defaultPronouns, personalPronouns);
            rm.addAttribute(defaultBio, bio);
            return "redirect:register?passwordFormatError";
        }

        if (!password.equals(confirmPassword)) {
            addModelAttribute(rm, username, firstName, middleName, lastName, email);
            rm.addAttribute(defaultNickName, nickName);
            rm.addAttribute(defaultPronouns, personalPronouns);
            rm.addAttribute(defaultBio, bio);
            return "redirect:register?passwordError";
        }

        boolean firstNameMatchesPattern = Pattern.compile("[^A-Za-z]").matcher(firstName).find();
        if (firstName.length() < 2) {
            firstNameMatchesPattern = true;
        }
        if (firstNameMatchesPattern) {
            addModelAttribute(rm, username, firstName, middleName, lastName, email);
            rm.addAttribute(defaultNickName, nickName);
            rm.addAttribute(defaultPronouns, personalPronouns);
            rm.addAttribute(defaultBio, bio);
            return "redirect:register?firstNameError";
        }

        boolean lastNameMatchesPattern = Pattern.compile("[^A-Za-z]").matcher(lastName).find();
        if (lastName.length() < 2) {
            lastNameMatchesPattern = true;
        }
        if (lastNameMatchesPattern) {
            addModelAttribute(rm, username, firstName, middleName, lastName, email);
            rm.addAttribute(defaultNickName, nickName);
            rm.addAttribute(defaultPronouns, personalPronouns);
            rm.addAttribute(defaultBio, bio);
            return "redirect:register?lastNameError";
        }

        if (userBio.isEmpty()) {
            userBio = "Default Bio";
        }

        if (userNickName.length() > 50) {
            addModelAttribute(rm, username, firstName, middleName, lastName, email);
            rm.addAttribute(defaultBio, bio);
            rm.addAttribute(defaultPronouns, personalPronouns);
            return "redirect:register?nickNameError";
        }

        if (userBio.length() > 100) {
            addModelAttribute(rm, username, firstName, middleName, lastName, email);
            rm.addAttribute(defaultNickName, nickName);
            rm.addAttribute(defaultPronouns, personalPronouns);

            return "redirect:register?bioError";
        }

        try {
            registrationReply = registerClientService.receiveConformation(username, password, firstName, middleName, lastName, email, userBio, personalPronouns, nickName);
        } catch (Exception e) {
            model.addAttribute("err", "Error connecting to Identity Provider...");
            return "redirect:register?idpConnectionError";
        }
        if (registrationReply.getIsSuccess()) {
            loginReply = authenticateClientService.authenticate(username, password);
            var domain = request.getHeader("host");
            CookieUtil.create(
                    response,
                    "lens-session-token",
                    loginReply.getToken(),
                    true,
                    5 * 60 * 60, // Expires in 5 hours
                    domain.startsWith("localhost") ? null : domain
            );
            rm.addAttribute("userId", (int)loginReply.getUserId());
            return "redirect:account";
        } else {
            if (registrationReply.getMessage().equals("Username taken")) {
                model.addAttribute("err", "Username Taken");
                addModelAttribute(rm, username, firstName, middleName, lastName, email);
                rm.addAttribute(defaultNickName, nickName);
                rm.addAttribute(defaultPronouns, personalPronouns);
                rm.addAttribute(defaultBio, bio);

                return "redirect:register?usernameError";
            } else {
                model.addAttribute("err", "Something went wrong");
                addModelAttribute(rm, username, firstName, middleName, lastName, email);
                return "redirect:register?registerError";
            }

        }
    }

    /**
     * Method to add model attribute(current user's inputs) to the register page, Save user's information on form
     * @param rm RedirectAttributes
     * @param username user's username
     * @param firstName user's first name
     * @param middleName user's middle name
     * @param lastName user's last name
     * @param email user's email
     */
    private void addModelAttribute(RedirectAttributes rm,
                                   String username,
                                   String firstName,
                                   String middleName,
                                   String lastName,
                                   String email) {
        rm.addAttribute(defaultUsername, username);
        rm.addAttribute(defaultFirstName, firstName);
        rm.addAttribute(defaultMiddleName, middleName);
        rm.addAttribute(defaultLastName, lastName);
        rm.addAttribute(defaultEmail, email);
    }


    /**
     * Method to check if password is in right format
     * @param password user's password
     * @return false if any formatting issue
     */
    public boolean isValid(String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
