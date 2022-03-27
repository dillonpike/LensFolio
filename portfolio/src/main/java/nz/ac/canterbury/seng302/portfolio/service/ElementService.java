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
                model.addAttribute("updateMessage", "Account Information Successfully Updated");
            } else {
                model.addAttribute("isUpdateSuccess", false);
                model.addAttribute("updateMessage", "Update Canceled! Something went wrong!");
            }
        }
        return model;
    }
}
