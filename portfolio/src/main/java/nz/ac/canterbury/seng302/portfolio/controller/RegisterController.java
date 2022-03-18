package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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


    @GetMapping("/register")
    public String registration() {
        return "register";
    }

    @PostMapping("/register")
    public String registration(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "lastName") String lastName,
            @RequestParam(name = "username") String username,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            Model model
    ) {
        UserRegisterResponse registrationReply;

        //TODO Pass the data to check if any duplicated username instead of <authenticate>

        try {
            registrationReply = registerClientService.receiveConformation(username, password, firstName, lastName, email);
        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            return "login";
        }
        if (!registrationReply.getIsSuccess()) {
            return "account";
        } else {
            return "login";
        }
    }
}
