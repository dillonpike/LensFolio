package nz.ac.canterbury.seng302.portfolio.service;

import java.util.List;
import java.util.Optional;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Tag;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
      tagRepository.deleteById(tag.getTagId());
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
  public List<Tag> getTags(int evidenceId) {
    return tagRepository.findByEvidenceId(evidenceId);
  }

  /**
   * Remove a tag from the database.
   * @param tagId Id of the tag being removed
   */
  public void removeTag(int tagId) {
    Optional<Tag> sOptional = tagRepository.findById(tagId);

    if (sOptional.isPresent()) {
      Tag tag = sOptional.get();
      tagRepository.deleteById(tag.getTagId());
    }
  }

  /**
   * Remove tags from the database that aren't connected to any pieces of evidence.
   */
  public void removeTagsWithNoEvidence() {
    List<Tag> tags = tagRepository.findAll();
    for (Tag tag : tags) {
      int tagId = tag.getTagId();
      List<Evidence> evidences = evidenceRepository.findAllByTagId(tagId);
      if (evidences.isEmpty()) {
        removeTag(tagId);
      }
    }
  }

}
