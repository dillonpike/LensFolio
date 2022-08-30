package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller for evidence endpoints.
 */
@Controller
public class EvidenceController {

    /**
     * Method tries to add and sve the new evidence piece to the database
     * @param evidence Evidence piece with information.
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param httpServletResponse for adding status codes to
     * @return redirect user to evidence tab, or keep up modal if there are errors.
     */
    @PostMapping("/add-evidence")
    public String addEvidence(
            @ModelAttribute("evidence") Evidence evidence,
            Model model,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal AuthState principal
    ) {
        // TODO Implement this method
        // Comments based on how the group controller implements adding groups.
        // * Add the evidence to the repo and get back a response *
        if (false) { // * someResponse.isSuccessful() check *
            // * Add the evidence to the model *
            // * Maybe add something to the model to make sure the evidence tab is shown? *
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return "account"; // * return some sort of evidence fragment? *
        }

        // else
        // * get errors from someResponse *
        // * Add the errors to the model *

        // Test code for showing the feature is not yet implemented.
        String errorMessage = "Feature not yet implemented";
        model.addAttribute("evidenceTitleAlertMessage", errorMessage);

        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        return "fragments/evidenceModal::evidenceModalBody";
    }
}
