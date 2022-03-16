package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class editPasswordController {

    @Autowired
    private AuthenticateClientService authenticateClientService;

    /***
     * Generate the edit password page which let user change password
     *
     * @return The edit password page
     */
    @GetMapping("/editPassword")
    public String showEditPassword(
    ) {
        return "editPassword";
    }
}
