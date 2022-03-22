package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;


@Controller
public class RegisterController {


    @Autowired
    private RegisterClientService registerClientService;


    @GetMapping("/register")
    public String registration() {
        return "registration";
    }

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

        //TODO Pass the data to check if any duplicated username instead of <authenticate>
//        registrationReply = registerClientService.receiveConformation(username, password, firstName, lastName, email);

        if (!password.equals(confirmPassword)) {
            return "redirect:register?passwordError";
        }
        try {
            registrationReply = registerClientService.receiveConformation(username, password, firstName, middleName, lastName, email);
        } catch (Exception e) {
            model.addAttribute("err", "Error connecting to Identity Provider...");
            System.out.println("registerController; Failed connecting to Identity Provider");
            e.printStackTrace();
            return "login";
        }
        if (registrationReply.getIsSuccess()) {
            return "redirect:register?successfulRegister";
        } else {
            model.addAttribute("err", "Something went wrong");
            System.out.println("registerController; Failed to register user");
            return "registration";
        }
    }
}
