package nz.ac.canterbury.seng302.portfolio.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Tag;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Contains methods for saving, deleting, and retrieving tag objects to the database.
 */
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    /**
    * This function returns a tag based on the tagId.
    * @param tagId id of tag looking to return
    * @return Tag.
    */
    public Tag getTag(int tagId) {
        Optional<Tag> sOptional = tagRepository.findById(tagId);
        Tag tag = null;
        if (sOptional.isPresent()) {
            tag = sOptional.get();
        }
        return tag;
    }

    /**
     * Returns a tag from the database with the given tag name. Ignores case.
     * @param tagName name of the tag
     * @return tag with the given name
     */
    public Tag getTagByNameIgnoreCase(String tagName) {
        List<Tag> tags = tagRepository.findByTagNameIgnoreCase(tagName);
        Tag tag = null;
        if (!tags.isEmpty()) {
            tag = tags.get(0);
        }
        return tag;
    }

    /**
    * This function returns all tags ordered alphabetically.
    * @return List of tags.
    */
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    /**
    * This function returns all tags based on the evidenceId.
    * @param evidenceId id for piece of evidence
    * @return List of tags.
    */
    public Set<Tag> getTags(int evidenceId) {
        Optional<Evidence> evidence = evidenceRepository.findById(evidenceId);
        Set<Tag> tags = null;
        if (evidence.isPresent()) {
            Evidence evidence1 = evidence.get();
            tags = evidence1.getTags();
        }
        return tags;
    }


    /**
     * Remove a tag from the database.
     * @param tagId Id of the tag being removed
     * @return true if removed, otherwise false
     */
    public boolean removeTag(int tagId) {
        Optional<Tag> sOptional = tagRepository.findById(tagId);
        if (sOptional.isPresent()) {
            Tag tag = sOptional.get();
            tagRepository.deleteById(tag.getTagId());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all tags that a user has used.
     * @param userId user id of the user
     * @return list of tags the user has used
     */
    public List<Tag> getTagsFromUserId(int userId) {
        List<Evidence> evidenceList = evidenceRepository.findAllByUserId(userId);
        Set<Tag> allTags = new HashSet<>();

        for (Evidence evidence : evidenceList) {
            Set<Tag> tags = evidence.getTags();
            allTags.addAll(tags);
        }
        return allTags.stream().toList();
    }
}

