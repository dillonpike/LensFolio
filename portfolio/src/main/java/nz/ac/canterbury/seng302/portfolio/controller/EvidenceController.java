package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Tag;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private ElementService elementService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    private static final String ADD_EVIDENCE_MODAL_FRAGMENT = "fragments/evidenceModal::evidenceModalBody";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE = "evidenceTitleAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_DESCRIPTION_MESSAGE = "evidenceDescriptionAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_DATE_MESSAGE = "evidenceDateAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_WEB_LINKS_MESSAGE = "evidenceWebLinksAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_SKILL_TAGS_MESSAGE = "evidenceSkillTagsAlertMessage";

    /**
     * Method tries to add and sve the new evidence piece to the database
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
     * Method to display the main page for viewing skill specific pieces of evidence.
     * @param model         Parameters sent to thymeleaf template to be rendered into HTML.
     * @param principal     Used for authentication of a user.
     * @param userId        The id of the current user.
     * @param skillId       The id of the current skill being searched for.
     * @return              redirect user to evidence tab, or keep up modal if there are errors.
     */
    @GetMapping("/evidence-skills")
    public String evidenceSkillPage(
            Model model,
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "userId") int userId,
            @RequestParam(value = "skillId") int skillId
    ) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);

        List<Evidence> evidenceList;
        Tag skillTag;

        try {
            evidenceList = evidenceService.getEvidencesWithSkill(skillId);
            skillTag = tagService.getTag(skillId);
            if (skillTag == null) {
                throw new NullPointerException("Invalid Tag Id");
            }
        } catch (NullPointerException e) {
            return "redirect:account?userId=" + id;
        }

        model.addAttribute("evidencesExists", ((evidenceList != null) && (!evidenceList.isEmpty())));
        model.addAttribute("evidences", evidenceList);
        model.addAttribute("skillTag", skillTag);

        model.addAttribute("viewedUserId", userId);
        model.addAttribute("skillId", skillId);
        return "evidence";
    }

    @GetMapping("/switch-evidence-list")
    public String membersWithoutAGroupCard(
            Model model,
            @RequestParam(value = "userId") int userId,
            @RequestParam(value = "viewedUserId") int viewedUserId,
            @RequestParam(value = "listAll") boolean listAll,
            @RequestParam(value = "skillId") int skillId
    ) {
        List<Evidence> evidenceList;
        try {
            if (listAll) {
                evidenceList = evidenceService.getEvidencesWithSkill(skillId);
            } else {
                evidenceList = evidenceService.getEvidencesWithSkillAndUser(viewedUserId, skillId);
            }
        } catch (NullPointerException e) {
            return "redirect:account?userId=" + userId;
        }
        model.addAttribute("evidencesExists", ((evidenceList != null) && (!evidenceList.isEmpty())));
        model.addAttribute("evidences", evidenceList);

        return "evidence::evidenceList";
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
