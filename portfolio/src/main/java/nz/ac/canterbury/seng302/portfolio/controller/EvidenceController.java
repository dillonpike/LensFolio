package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Tag;
import nz.ac.canterbury.seng302.portfolio.model.NotificationMessage;
import nz.ac.canterbury.seng302.portfolio.model.NotificationResponse;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.TagService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAcceptableException;
import java.util.List;

/**
 * Controller for evidence endpoints.
 */
@Controller
public class EvidenceController {

    Logger logger = LoggerFactory.getLogger(EvidenceController.class);

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    private static final String ADD_EVIDENCE_MODAL_FRAGMENT = "fragments/evidenceModal::evidenceModalBody";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE = "evidenceTitleAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_DESCRIPTION_MESSAGE = "evidenceDescriptionAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_DATE_MESSAGE = "evidenceDateAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_WEB_LINKS_MESSAGE = "evidenceWebLinksAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_SKILL_TAGS_MESSAGE = "evidenceSkillTagsAlertMessage";

    /**
     * Method tries to add and sqve the new evidence piece to the database.
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param httpServletResponse for adding status codes to
     * @return redirect user to evidence tab, or keep up modal if there are errors
     */
    @PostMapping("/add-evidence")
    public String addEvidence(
            @ModelAttribute("evidence") Evidence evidence,
            Model model,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal AuthState principal
    ) {
        try {
            evidenceService.validateEvidence(evidence, model);
            boolean wasAdded = evidenceService.addEvidence(evidence);
            if (wasAdded) {
                // * Add the evidence to the model *
                // * Maybe add something to the model to make sure the evidence tab is shown? *
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                return "fragments/evidenceModal::evidenceModalBody"; // * return some sort of evidence fragment? *
            } else {
                String errorMessage = "Evidence Not Added. Saving Error Occurred.";
                model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, errorMessage);
                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return ADD_EVIDENCE_MODAL_FRAGMENT;
            }

        } catch (NotAcceptableException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Attributes of evidence not formatted correctly. Not adding evidence. ");
            return ADD_EVIDENCE_MODAL_FRAGMENT;
        }
    }

    /**
     * Returns a list of the ids and names of skills used by the user.
     * @param userId user id of the user
     * @return set of tag names
     */
    @GetMapping("/get-skills")
    @ResponseBody
    public List<List<String>> getSkills(@RequestParam("userId") int userId) {
        List<Tag> skills = tagService.getTagsFromUserId(userId);
        return List.of(skills.stream().map(tag -> String.valueOf(tag.getTagId())).toList(),
                skills.stream().map(Tag::getTagName).toList());
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This is triggered when a user adds a piece of evidence.
     * @param message Information about the added piece of evidence.
     * @return Returns the message given.
     */
    @MessageMapping("/evidence-add")
    @SendTo("/webSocketGet/evidence-added")
    public NotificationResponse evidenceAddNotification(NotificationMessage message) {
        return NotificationResponse.fromMessage(message, "add");
    }
}
