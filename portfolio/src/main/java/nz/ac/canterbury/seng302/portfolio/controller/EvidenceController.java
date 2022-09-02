package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Controller for evidence endpoints.
 */
@Controller
public class EvidenceController {

    Logger logger = LoggerFactory.getLogger(EvidenceController.class);

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    private static final String ADD_EVIDENCE_MODAL_FRAGMENT = "fragments/evidenceModal::evidenceModalBody";

    private static final String ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE = "evidenceTitleAlertMessage";

    /**
     * Method tries to add and sve the new evidence piece to the database
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param httpServletResponse for adding status codes to
     * @return redirect user to evidence tab, or keep up modal if there are errors.
     */
    @PostMapping("/add-evidence")
    public String addEvidence(
            HttpServletRequest request,
            Model model,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal AuthState principal
    ) {
        try {
            String title = request.getParameter("evidenceTitle");
            String description = request.getParameter("evidenceDescription");
            Date date = Project.stringToDate(request.getParameter("evidenceDate"));
            int projectId = 0;
            int userId = userAccountClientService.getUserIDFromAuthState(principal);
            Evidence evidence = new Evidence(projectId, userId, title, description, date);

            boolean wasAdded = evidenceService.addEvidence(evidence);
            if (wasAdded) {
                // * Add the evidence to the model *
                // * Maybe add something to the model to make sure the evidence tab is shown? *
                String successMessage = "Evidence Added. ";
                model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, successMessage);
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                return "fragments/evidenceModal::evidenceModalBody"; // * return some sort of evidence fragment? *
            }

            // else
            String errorMessage = "Evidence Not Added. Saving Error Occurred.";
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, errorMessage);
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ADD_EVIDENCE_MODAL_FRAGMENT;

        } catch (NullPointerException e) {
            String errorMessage = "Evidence Not Added. Error Finding Attributes.";
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, errorMessage);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("Unable to find attributes of evidence for adding evidence");
            return ADD_EVIDENCE_MODAL_FRAGMENT;
        }


    }
}
