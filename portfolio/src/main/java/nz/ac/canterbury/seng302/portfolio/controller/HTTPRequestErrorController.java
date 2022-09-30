package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/***
 * Controller receive HTTP GET, POST, PUT, DELETE calls for error page
 */
@Controller
public class HTTPRequestErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    /***
     * Request Mapping Method
     *
     * Handles Errors that are caused by invalid URL's and links them to custom
     * error pages in which can redirect to a proper page
     *
     * @param request HTTP request sent to this endpoint
     * @param principal Authentication principal
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return HTML page to show an error
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request,
                              @AuthenticationPrincipal AuthState principal,
                              Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "redirect:login?forbidden";
            }

            Integer id = userAccountClientService.getUserIDFromAuthState(principal);
            elementService.addHeaderAttributes(model, id);
            model.addAttribute("userId", id);

            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "500InternalServer";
            }
            else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "404NotFound";
            } else {
                //technically any other error
                return "404NotFound";
            }
        }
        return "error";
    }
}