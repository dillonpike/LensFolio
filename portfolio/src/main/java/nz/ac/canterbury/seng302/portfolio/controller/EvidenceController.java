package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.*;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAcceptableException;
import java.util.ArrayList;
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
    private PermissionService permissionService;

    @Autowired
    private ElementService elementService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private RegisterClientService registerClientService;

    private static final String ADD_EVIDENCE_MODAL_FRAGMENT = "fragments/evidenceModal::evidenceModalBody";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE = "evidenceTitleAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_DESCRIPTION_MESSAGE = "evidenceDescriptionAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_DATE_MESSAGE = "evidenceDateAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_WEB_LINKS_MESSAGE = "evidenceWebLinksAlertMessage";

    public static final String ADD_EVIDENCE_MODAL_FRAGMENT_SKILL_TAGS_MESSAGE = "evidenceSkillTagsAlertMessage";

    public static final String ACCOUNT_EVIDENCE = "fragments/evidenceList::evidenceList";


    /**
     * Method tries to add and save the new evidence piece to the database.
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
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                String errorMessage = "Evidence Not Added. Saving Error Occurred.";
                model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, errorMessage);
                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (NotAcceptableException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Attributes of evidence not formatted correctly. Not adding evidence. ");
        }

        return ADD_EVIDENCE_MODAL_FRAGMENT;
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

        List<Integer> evidenceHighFivedIds = new ArrayList<>();
        for (Evidence eachEvidence:evidenceList) {
            eachEvidence.setUser(registerClientService.getUserData(eachEvidence.getUserId()));
            if (eachEvidence.getHighFivers().stream().map(HighFivers::getUserId).anyMatch(x -> x.equals(id))) {
                evidenceHighFivedIds.add(eachEvidence.getEvidenceId());
            }
        }

        model.addAttribute("evidencesExists", ((evidenceList != null) && (!evidenceList.isEmpty())));
        model.addAttribute("evidences", evidenceList);
        model.addAttribute("skillTag", skillTag);
        model.addAttribute("evidenceHighFivedIds", evidenceHighFivedIds);

        model.addAttribute("viewedUserId", userId);
        model.addAttribute("currentUserId", id);
        model.addAttribute("skillId", skillId);
        return "evidence";
    }

    /**
     * Method to handle a partial refresh of the list of evidences to either display all the evidence with a given skill ID
     * or all evidence with both a given skill ID and user ID attached to it.
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param userId The user currently logged in.
     * @param viewedUserId The user that should be attached to the evidence.
     * @param listAll A boolean value on if all or only a certain viewedUsers evidence should be returned.
     * @param skillId The skill that needs to be attached to the evidence.
     * @return A fragment of the list of evidences to display.
     */
    @GetMapping("/switch-evidence-list")
    public String membersWithoutAGroupCard(
            Model model,
            @RequestParam(value = "userId") int userId,
            @RequestParam(value = "viewedUserId") int viewedUserId,
            @RequestParam(value = "listAll") boolean listAll,
            @RequestParam(value = "skillId") int skillId,
            @AuthenticationPrincipal AuthState principal
    ) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        List<Evidence> evidenceList;
        List<Integer> evidenceHighFivedIds = new ArrayList<>();
        try {
            if (listAll) {
                evidenceList = evidenceService.getEvidencesWithSkill(skillId);

            } else {
                evidenceList = evidenceService.getEvidencesWithSkillAndUser(viewedUserId, skillId);
            }
        } catch (NullPointerException e) {
            return "redirect:account?userId=" + userId;
        }

        for (Evidence eachEvidence:evidenceList) {
            eachEvidence.setUser(registerClientService.getUserData(eachEvidence.getUserId()));
            if (eachEvidence.getHighFivers().stream().map(HighFivers::getUserId).anyMatch(x -> x.equals(id))) {
                evidenceHighFivedIds.add(eachEvidence.getEvidenceId());
            }
        }

        model.addAttribute("evidencesExists", ((evidenceList != null) && (!evidenceList.isEmpty())));
        model.addAttribute("evidences", evidenceList);
        model.addAttribute("evidenceHighFivedIds", evidenceHighFivedIds);
        model.addAttribute("viewedUserId", userId);
        model.addAttribute("currentUserId", id);

        return "fragments/evidenceList::evidenceList";
    }

    /**
     * Returns a list of the ids and names of skills used by the user.
     * @param userId user id of the user
     * @return list of ids and names of skills used by the user
     */
    @GetMapping("/get-skills")
    @ResponseBody
    public List<List<String>> getSkills(@RequestParam("userId") int userId) {
        List<Tag> skills = tagService.getTagsFromUserId(userId);
        return List.of(skills.stream().map(tag -> String.valueOf(tag.getTagId())).toList(),
                skills.stream().map(Tag::getTagName).toList());
    }

    /**
     * Returns a list of the ids and names of skills used by any user.
     * @return list of ids and names of all skills
     */
    @GetMapping("/get-all-skills")
    @ResponseBody
    public List<List<String>> getAllSkills() {
        List<Tag> skills = tagService.getAllTags();
        return List.of(skills.stream().map(tag -> String.valueOf(tag.getTagId())).toList(),
                skills.stream().map(Tag::getTagName).toList());
    }


    /**
     * Saves a piece of evidence after being high-fived.
     * @param evidenceId evidence id of the piece of evidence being high-fived
     * @param userId user id of the owner of the piece of evidence
     * @param userName userName of the owner of the piece of evidence
     * @return a redirect to load the page
     */
    @PostMapping("saveHighFiveEvidence")
    public String saveHighFiveEvidence(
            @RequestParam("evidenceId") int evidenceId,
            @RequestParam("userId") int userId,
            @RequestParam("userName") String userName,
            Model model,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal AuthState principal
    ) {
        boolean wasHighFived = evidenceService.saveHighFiveEvidence(evidenceId, userId, userName);
        if (wasHighFived) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ACCOUNT_EVIDENCE;
    }

    /**
     * Saves a piece of evidence after being un-high-fived.
     * @param evidenceId evidence id of the piece of evidence being un-high-fived
     * @param userId user id of the owner of the piece of evidence
     * @param userName userName of the owner of the piece of evidence
     * @return a redirect to load the page
     */
    @PostMapping("removeHighFiveEvidence")
    public String removeHighFiveEvidence(
            @RequestParam("evidenceId") int evidenceId,
            @RequestParam("userId") int userId,
            @RequestParam("userName") String userName,
            Model model,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal AuthState principal
    ) {
        boolean wasRemoved = evidenceService.removeHighFiveEvidence(evidenceId, userId, userName);
        if (wasRemoved) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ACCOUNT_EVIDENCE;
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

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This is triggered when a user deletes a piece of evidence.
     * @param message Information about the deleted piece of evidence.
     * @return Returns the message given.
     */
    @MessageMapping("/evidence-delete")
    @SendTo("/webSocketGet/evidence-deleted")
    public NotificationResponse evidenceDeleteNotification(NotificationMessage message) {
        return NotificationResponse.fromMessage(message, "delete");
    }

    /***
     * Used to handle the interaction between a piece of evidence being highfived
     * and the notification being shown through the header.
     *
     * @return Send a notification to the header to display a highfive notification.
     */
    @MessageMapping("/high-fived-evidence")
    @SendTo("/webSocketGet/notification-of-highfive")
    public NotificationHighFive highFiveNotification(NotificationHighFive notificationHighFive) {
        return notificationHighFive;
    }

    /***
     * Used to handle the interaction between a piece of evidence being un-highfived
     * @return Send a message to reload the page if viewing.
     */
    @MessageMapping("/remove-high-fived-evidence")
    @SendTo("/webSocketGet/notification-of-remove-highfive")
    public NotificationHighFive removeHighFiveNotification(NotificationHighFive notificationHighFive) {
        return notificationHighFive;
    }
}
