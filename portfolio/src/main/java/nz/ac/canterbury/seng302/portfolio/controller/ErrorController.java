package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;



@ControllerAdvice
@Controller
public class ErrorController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private RegisterClientService registerClientService;

    @GetMapping("/404NotFound")
    public String show404ErrorPage(
            Model model,
            @ModelAttribute("userId") int userId
    ) {
        model.addAttribute("userId", userId);
        return "404NotFound";
    }

    /***
     * This method is to catch and handle all MissingServletRequestParameterException
     * for the whole application
     *
     * @param principal
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return 404 Error Not Found page
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParams(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        UserResponse getUserByIdReplyHeader;

        Integer id = userAccountService.getUserIDFromAuthState(principal);
        getUserByIdReplyHeader = registerClientService.getUserData(id);
        String fullNameHeader = getUserByIdReplyHeader.getFirstName() + " " + getUserByIdReplyHeader.getMiddleName() + " " + getUserByIdReplyHeader.getLastName();
        model.addAttribute("headerFullName", fullNameHeader);
        model.addAttribute("userId", id);
        return "404NotFound";
    }
}
