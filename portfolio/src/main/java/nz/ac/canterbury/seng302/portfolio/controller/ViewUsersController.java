package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewUsersController {

    /***
     * HTTP GET method request handler when url is "/viewUsers"
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param principal
     * @return
     */
    @GetMapping("/viewUsers")
    public String showUserTablePage(
            Model model,
            @AuthenticationPrincipal AuthState principal
    ) {
        return "viewUsers";
    }
}
