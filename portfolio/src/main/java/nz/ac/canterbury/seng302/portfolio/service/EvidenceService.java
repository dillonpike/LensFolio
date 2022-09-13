package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.WebLink;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
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
        Pattern webLinkRegex = Pattern.compile("^(https?://)?" + // validate protocol
                "((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|" + // validate domain name
                "((\\d{1,3}\\.){3}\\d{1,3}))" + // validate OR ip (v4) address
                "(:\\d+)?(/[-a-z\\d%_.~+]*)*" + // validate port and path
                "(\\?[;&a-z\\d%_.~+=-]*)?" + // validate query string
                "(#[-a-z\\d_]*)?$");
        int maxNumWebLinks = 10;
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
        if (hasError) {
            throw new NotAcceptableException("Evidence fields have errors");
        }
    }

}
