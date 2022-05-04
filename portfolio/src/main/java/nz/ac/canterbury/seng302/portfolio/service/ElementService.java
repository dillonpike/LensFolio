package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class ElementService {

    @Autowired
    private RegisterClientService registerClientService;

    /**
     * Updates the given model with an updateMessage attribute.
     *
     * If isUpdateSuccess in the request is true, updateMessage will be set to successMessage from the request, or a
     * default success message if successMessage doesn't exist. If isUpdateSuccess is false, updateMessage will be set
     * to failureMessage from the request, or a default failure message if failureMessage doesn't exist.
     *
     * @param model model from controller method
     * @param request HTTP request from controller method
     */
    public void addUpdateMessage(Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            boolean isUpdateSuccess = (boolean) inputFlashMap.get("isUpdateSuccess");
            if (isUpdateSuccess) {
                model.addAttribute("isUpdateSuccess", true);
                String message = inputFlashMap.containsKey("successMessage") ?
                        (String) inputFlashMap.get("failureMessage") : "Account Information Successfully Updated";
                model.addAttribute("updateMessage", message);
            } else {
                model.addAttribute("isUpdateSuccess", false);
                String message = inputFlashMap.containsKey("failureMessage") ?
                        (String) inputFlashMap.get("failureMessage") : "Update Canceled! Something went wrong!";
                model.addAttribute("updateMessage", message);
            }
        }
    }

    public void addHeaderAttributes(Model model, int userId) {
        UserResponse userData = registerClientService.getUserData(userId);
        String fullNameHeader = userData.getFirstName() + " " + userData.getMiddleName() + " " + userData.getLastName();
        model.addAttribute("headerFullName", fullNameHeader);
    }
}
