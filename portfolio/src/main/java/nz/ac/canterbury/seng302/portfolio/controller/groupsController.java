package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for group page
 */
@Controller
public class groupsController {


    @GetMapping("/groups")
    public String groups() {
        return "groups";
    }
}
