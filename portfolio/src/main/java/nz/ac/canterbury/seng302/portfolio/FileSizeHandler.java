package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FileSizeHandler {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handle(@AuthenticationPrincipal AuthState principal, RedirectAttributes ra) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        ra.addFlashAttribute("errors", "Uploaded file is too large.");

        ra.addAttribute("userId", id);
        ra.addFlashAttribute("isUpdateSuccess", false);

        return "redirect:editAccount";
    }
}
