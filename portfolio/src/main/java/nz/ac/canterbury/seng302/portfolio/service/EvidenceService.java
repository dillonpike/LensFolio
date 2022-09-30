package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Tag;
import nz.ac.canterbury.seng302.portfolio.model.HighFivers;
import nz.ac.canterbury.seng302.portfolio.model.WebLink;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.repository.HighFiversRepository;
import nz.ac.canterbury.seng302.portfolio.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.ws.rs.NotAcceptableException;
import java.util.*;
import java.util.Date;

import static nz.ac.canterbury.seng302.portfolio.controller.EvidenceController.*;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Contains methods for saving, deleting, updating and retrieving evidence objects to the database.
 */
@Service
public class EvidenceService {

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private HighFiversRepository highFiversRepository;

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
     * This function get an evidence based on evidence Id.
     * @param evidenceId the ID of an evidence in interest
     * @return List of evidences.
     */
    public Evidence getEvidence(int evidenceId) {
        try{
            return evidenceRepository.findByEvidenceId(evidenceId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Remove an evidence from the database.
     * @param id ID of the evidence being removed
     */
    public boolean removeEvidence(Integer id) {
        Optional<Evidence> sOptional = evidenceRepository.findById(id);
        if (sOptional.isPresent()) {
            Evidence evidence = sOptional.get();
            Set<Tag> tags = null;
            Set<WebLink> webLinks = null;
            Set<HighFivers> highFivers = null;

            if (!evidence.getTags().isEmpty()) {
                tags = Set.copyOf(evidence.getTags());
                for (Tag tag : tags) {
                    evidence.removeTag(tag);
                    tag.getEvidence().remove(evidence);
                    tagRepository.save(tag);
                }
            }

            if (!evidence.getWebLinks().isEmpty()) {
                webLinks = Set.copyOf(evidence.getWebLinks());
                evidence.setWebLinks(new HashSet<>());
            }

            if (!evidence.getHighFivers().isEmpty()) {
                highFivers = Set.copyOf(evidence.getHighFivers());
                evidence.setHighFivers(new HashSet<>());
            }

            evidenceRepository.delete(evidenceRepository.save(evidence));

            // Check to see if the evidence was deleted
            Optional<Evidence> evidenceStillThere = evidenceRepository.findById(id);
            if (evidenceStillThere.isPresent()) {
                // Add the users back since deleting the group did not work
                Evidence emptyEvidence = evidenceStillThere.get();
                if (tags != null) {
                    List<Tag> tagList = new ArrayList<>(tags);
                    for (Tag tag : tagList) {
                        emptyEvidence.addTag(tag);
                    }
                }
                emptyEvidence.setWebLinks(webLinks);
                emptyEvidence.setHighFivers(highFivers);
                evidenceRepository.save(emptyEvidence);
                return false;
            }
            removeTagsWithNoEvidence();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Save a new evidence piece to the database.
     * @param newEvidence New evidence piece to be saved.
     * @return Whether the evidence was successfully saved.
     */
    public boolean addEvidence(Evidence newEvidence) {
        try {
            processTags(newEvidence);
            evidenceRepository.save(newEvidence);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Processes the tags on the given evidence and replaces
     * @param evidence piece of evidence to process tags for
     */
    private void processTags(Evidence evidence) {
        for (Tag tag : evidence.getTags()) {
            Tag databaseTag = tagService.getTagByNameIgnoreCase(tag.getTagName());
            if (databaseTag != null) {
                evidence.removeTag(tag);
                evidence.addTag(databaseTag);
            }
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
            model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_SKILL_TAGS_MESSAGE,
                "You can only have up to 10 skill tags");
            hasError = true;
        }
        for (Tag tag : evidence.getTags()) {
            if (tag.getTagName().length() < 1) {
                model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_SKILL_TAGS_MESSAGE, "Tags must have at least one character");
                hasError = true;
                break;
            }
            if (emojiRegex.matcher(tag.getTagName()).find()) {
                model.addAttribute(ADD_EVIDENCE_MODAL_FRAGMENT_SKILL_TAGS_MESSAGE, "Tags must not contain emojis");
                hasError = true;
                break;
            }
        }
        if (hasError) {
            throw new NotAcceptableException("Evidence fields have errors");
        }
    }

    /**
     * Saves a piece of evidence after being high-fived.
     * @param evidenceId evidence id of the piece of evidence being high-fived
     * @param userId user id of the owner of the piece of evidence
     * @param userName userName of the owner of the piece of evidence
     * @return boolean whether the piece of evidence was high-fived correctly
     */
    public boolean saveHighFiveEvidence(int evidenceId, int userId, String userName) {
        Optional<Evidence> evidenceOptional = evidenceRepository.findById(evidenceId);
        if (evidenceOptional.isPresent()) {
            Evidence evidence = evidenceOptional.get();
            if (evidence.getHighFiverIds().contains(userId)) {
                return false;
            }
            HighFivers newHighFiver = highFiversRepository.save(new HighFivers(userName, userId));
            evidence.addHighFivers(newHighFiver);
            evidenceRepository.save(evidence);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Saves a piece of evidence after being un-high-fived.
     * @param evidenceId evidence id of the piece of evidence being un-high-fived
     * @param userId user id of the owner of the piece of evidence
     * @param userName userName of the owner of the piece of evidence
     * @return boolean whether the piece of evidence was un-high-fived correctly
     */
    public boolean removeHighFiveEvidence(int evidenceId, int userId, String userName) {
        Optional<Evidence> evidenceOptional = evidenceRepository.findById(evidenceId);
        if (evidenceOptional.isPresent()) {
            Evidence evidence = evidenceOptional.get();
            if (!evidence.getHighFiverIds().contains(userId)) {
                return false;
            }
            Set<HighFivers> highFivers = Set.copyOf(evidence.getHighFivers());
            for (HighFivers highFiver : highFivers) {
                if (highFiver.getUserId() == userId) {
                    evidence.removeHighFivers(highFiver);
                    highFiversRepository.delete(highFiver);
                    break;
                }
            }
            evidenceRepository.save(evidence);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks to ensure that a piece of evidence has the user with a userId,
     * matching that passed to the method, attached to it.
     * @param evidenceId    The evidence being checked.
     * @param userId        The user that needs to be attached to the evidence.
     * @return  Boolean on is the user is part of the evidence.
     */
    private boolean isUserAttached(int evidenceId, int userId){
        Optional<Evidence> sOptional = evidenceRepository.findById(evidenceId);
        boolean exists = false;

        if (sOptional.isPresent()) {
            Evidence evidence = sOptional.get();
            if (evidence.getUserId() == userId) {
                exists = true;
            }
        }
        return exists;
    }

    /**
     * Gets all pieces of evidences that have a certain skill and also orders them in reveres chronological order.
     * @param skillId   The skill that needs to be attached to the evidence.
     * @return          List of evidence with a given skill.
     */
    public List<Evidence> getEvidencesWithSkill(int skillId) throws NullPointerException{
        Tag tag = tagService.getTag(skillId);
        return tag.getEvidence().stream().sorted((o1, o2)->o2.getDate().
                compareTo(o1.getDate())).toList();
    }

    /**
     * Gets all pieces of evidences that have a certain skill and user attached to it
     * and also orders them in reveres chronological order.
     * @param userId    The user that needs to be attached to the evidence.
     * @param skillId   The skill that needs to be attached to the evidence.
     * @return          List of evidence with a given skill and user attached.
     */
    public List<Evidence> getEvidencesWithSkillAndUser(int userId, int skillId) throws NullPointerException{
        Tag tag = tagService.getTag(skillId);
        return tag.getEvidence().stream().filter(evidence -> isUserAttached(evidence.getEvidenceId(), userId)).sorted((o1, o2)->o2.getDate().
                compareTo(o1.getDate())).toList();
    }

    /**
     * Gets all pieces of evidences that have a certain category and also orders them in reveres chronological order.
     * @param categoryId   The category that needs to be attached to the evidence.
     * @return          List of evidence with a given category.
     */
    public List<Evidence> getEvidencesWithCategory(int categoryId) throws NullPointerException{
        Category category = categoryService.getCategory(categoryId);
        return category.getEvidence().stream().sorted((o1, o2)->o2.getDate().
                compareTo(o1.getDate())).toList();
    }

    /**
     * Gets all pieces of evidences that have a certain category and user attached to it
     * and also orders them in reveres chronological order.
     * @param userId    The user that needs to be attached to the evidence.
     * @param categoryId   The category that needs to be attached to the evidence.
     * @return          List of evidence with a given category and user attached.
     */
    public List<Evidence> getEvidencesWithCategoryAndUser(int userId, int categoryId) throws NullPointerException{

        Category category = categoryService.getCategory(categoryId);
        return category.getEvidence().stream().filter(evidence -> isUserAttached(evidence.getEvidenceId(), userId)).sorted((o1, o2)->o2.getDate().
                compareTo(o1.getDate())).toList();
    }

    /**
     * Gets all evidences that do not have a skill attached to them.
     * @return  A list of evidences with no skills attached to them.
     */
    public List<Evidence> getEvidencesWithoutSkills() {
        // Used to convert an iterable to a stream.
        Stream<Evidence> targetStream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(evidenceRepository.findAll().iterator(), Spliterator.ORDERED),
                false);
        return targetStream.filter(evidence -> hasNoSkills(evidence.getEvidenceId())).sorted((o1, o2)->o2.getDate().
                compareTo(o1.getDate())).toList();
    }

    /**
     * Gets all evidences that do not have a skill attached to them. But do have a user attached.
     * @return  A list of evidences with no skills attached to them but a given user is attached.
     */
    public List<Evidence> getEvidencesWithUserAndWithoutSkills(int userId) {
        // Used to convert an iterable to a stream.
        Stream<Evidence> targetStream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(evidenceRepository.findAll().iterator(), Spliterator.ORDERED),
                false);

        return targetStream.filter(evidence -> hasNoSkills(evidence.getEvidenceId()))
                .filter(evidence -> isUserAttached(evidence.getEvidenceId(), userId))
                .sorted((o1, o2)->o2.getDate().compareTo(o1.getDate())).toList();
    }

    /**
     * Checks to see if a skill has any skills attached to them.
     * @param evidenceId    The id of the evidence being checked.
     * @return Returns true if the evidence has no skills attached to it.
     */
    private boolean hasNoSkills(int evidenceId){
        Optional<Evidence> sOptional = evidenceRepository.findById(evidenceId);
        boolean valid = false;

        if (sOptional.isPresent()) {
            Evidence evidence = sOptional.get();
            if (evidence.getTags().isEmpty()) {
                valid = true;
            }
        }

        return valid;
    }

    /**
     * Remove tags from the database that aren't connected to any pieces of evidence.
     */
    public void removeTagsWithNoEvidence() {
        List<Tag> tags = tagRepository.findAll();
        for (Tag tag : tags) {
            int tagId = tag.getTagId();
            List<Evidence> evidences = getEvidencesWithSkill(tagId);
            if (evidences.isEmpty()) {
                tagService.removeTag(tagId);
            }
        }
    }

    /**
     * Adds the data of the evidence author to each evidence object so the author can be displayed in the frontend.
     */
    public void addUserDataToEvidence(List<Evidence> evidences) {
        for (Evidence eachEvidence:evidences) {
            eachEvidence.setUser(registerClientService.getUserData(eachEvidence.getUserId()));
        }
    }

}
