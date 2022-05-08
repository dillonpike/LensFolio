package nz.ac.canterbury.seng302.portfolio;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FileSizeHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handle(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra){

        ra.addFlashAttribute("errors", "Uploaded file is too large.");
        ra.addAttribute("userId", request.getParameter("userId"));
        ra.addFlashAttribute("isUpdateSuccess", false);

        return "redirect:editAccount";
    }
}
