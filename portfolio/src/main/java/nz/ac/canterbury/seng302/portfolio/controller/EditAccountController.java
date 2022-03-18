package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import nz.ac.canterbury.seng302.portfolio.service.EditAccountClientService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class EditAccountController {

    @Autowired
    private EditAccountClientService editAccountClientService;

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

    @PostMapping("/editAccount")
    public String editAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "newFullName") String newFullName,
            @RequestParam(name = "newNickName") String newNickName,
            @RequestParam(name = "newEmail") String newEmail,
            @RequestParam(name = "newGender") String newGender,
            @RequestParam(name = "newBio") String newBio
    ) {
        return "editAccount";
    }
}
