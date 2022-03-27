package nz.ac.canterbury.seng302.portfolio.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class ErrorController {

    @GetMapping("/404NotFound")
    public String show404ErrorPage(
            Model model,
            @ModelAttribute("userId") int userId
    ) {
        model.addAttribute("userId", userId);
        return "404NotFound";
    }
}
