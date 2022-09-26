package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Tag;
import nz.ac.canterbury.seng302.portfolio.model.WebLink;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.ws.rs.NotAcceptableException;
import java.util.Date;

import static nz.ac.canterbury.seng302.portfolio.controller.EvidenceController.*;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Contains methods for saving, deleting, updating and retrieving evidence objects to the database.
 */
@Service
public class EvidenceService {

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private RegisterClientService registerClientService;

    /**
     * This function returns all evidences based on the userId.
     * @param userId the ID of a user who we want to get evidences for.
     * @return List of evidences.
     */
    public List<Evidence> getEvidences(int userId) {
        List<Evidence> listEvidences = evidenceRepository.findAllByUserId(userId);
        return listEvidences.stream().sorted((o1, o2)->o2.getDate().
                compareTo(o1.getDate())).toList();
    }

    /**
     * Save a new evidence piece to the database.
     * @param newEvidence New evidence piece to be saved.
     * @return Whether the evidence was successfully saved.
     */
    public boolean addEvidence(Evidence newEvidence) {
        try {
            evidenceRepository.save(newEvidence);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate an evidence piece. Does backend checking for fields to make sure they comply with the given bounds.
     * @param evidence Evidence piece to be validated.
     * @param model Model to add error messages to.
     * @throws NotAcceptableException Thrown if there are any errors with the evidence piece's fields.
     */
    public void validateEvidence(Evidence evidence, Model model) throws NotAcceptableException {
        Pattern regex = Pattern.compile("^[\\p{N}\\p{P}\\p{S}\\p{Zs}]+$");
        Pattern webLinkRegex = Pattern.compile("^(http(s)?://)[\\w.-]+(?:\\.[\\w\\\\.-]+)*[\\w\\-\\\\._~:/?#\\[\\]@!$&'()*+,;=]+$");
        Pattern emojiRegex = Pattern.compile("[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]");
        int maxNumWebLinks = 10;
        int maxNumSkillTags = 10;
        try {
            evidence.setTitle(evidence.getTitle().trim());
            evidence.setDescription(evidence.getDescription().trim());
        } catch (NullPointerException ignored) { // If the title or description is null, we don't need to do anything
            // as the later if statements catch it anyway.
        }
        boolean hasError = false;
        if (evidence.getTitle() == null || evidence.getTitle().isEmpty()) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, "Title is required");
            hasError = true;
        } else if (evidence.getTitle().length() < 2) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, "Title must be at least 2 characters");
            hasError = true;
        } else if (evidence.getTitle().length() > 30) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, "Title must be less than 30 characters");
            hasError = true;
        } else if (regex.matcher(evidence.getTitle()).matches()) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, "Title must contain at least one letter");
            hasError = true;
        } else if(emojiRegex.matcher(evidence.getTitle()).find()) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_TITLE_MESSAGE, "Title must not contain emojis");
            hasError = true;
        }

        if (evidence.getDescription() == null || evidence.getDescription().isEmpty()) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_DESCRIPTION_MESSAGE, "Description is required");
            hasError = true;
        } else if (evidence.getDescription().length() < 2) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_DESCRIPTION_MESSAGE, "Description must be at least 2 characters");
            hasError = true;
        } else if (evidence.getDescription().length() > 250) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_DESCRIPTION_MESSAGE, "Description must be less than 250 characters");
            hasError = true;
        } else if (regex.matcher(evidence.getDescription()).matches()) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_DESCRIPTION_MESSAGE, "Description must contain at least one letter");
            hasError = true;
        } else if (emojiRegex.matcher(evidence.getDescription()).find()) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_DESCRIPTION_MESSAGE, "Description must not contain emojis");
            hasError = true;
        }
        if (evidence.getDate() == null || evidence.getDate().before(new Date(0))) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_DATE_MESSAGE, "Correctly formatted date is required");
            hasError = true;
        }
        if (evidence.getWebLinks().size() > maxNumWebLinks) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_WEB_LINKS_MESSAGE, "You can only have up to 10 web links");
            hasError = true;
        }
        for (WebLink webLink : evidence.getWebLinks()) {
            if (!webLinkRegex.matcher(webLink.getUrl()).matches()) {
                model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_WEB_LINKS_MESSAGE, "Web links must be valid URLs");
                hasError = true;
                break;
            }
        }
        if (evidence.getTags().size() > maxNumSkillTags) {
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_SKILL_TAGS_MESSAGE, "You can only have up to 10 skill tags");
            hasError = true;
        } else {
            hasError = false;
        }
        for (Tag tag : evidence.getTags()) {
            if (tag.getTagName().length() < 1) {
                model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_SKILL_TAGS_MESSAGE, "Tags must have at least one character");
                hasError = true;
                break;
            }
        }
        if (hasError) {
            throw new NotAcceptableException("Evidence fields have errors");
        }
    }

    /**
     * Returns a list of all users that have high fived the piece of evidence.
     * @param evidence evidence to get high fivers of
     * @return list of all users that have high fived the piece of evidence
     */
    public List<UserResponse> getHighFivers(Evidence evidence) {
        return evidence.getHighFiverIds().stream().map(registerClientService::getUserData).toList();
    }
}
