package nz.ac.canterbury.seng302.portfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class ElementService {

    public Model addBanner(Model model, HttpServletRequest request) {
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
        return model;
    }
}
