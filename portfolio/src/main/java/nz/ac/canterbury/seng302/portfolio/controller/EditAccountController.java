package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EditAccountController {

    @Autowired
    private AuthenticateClientService authenticateClientService;

    /***
     * Generate the edit account page which let user edit info/attributes
     *
     * @return The edit account page
     */
    @GetMapping("/editAccount")
    public String showEditAccountPage(
    ) {
       return "editAccount";
    }
}
