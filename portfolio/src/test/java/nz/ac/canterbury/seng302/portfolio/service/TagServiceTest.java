package nz.ac.canterbury.seng302.portfolio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Tag;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the {@link TagService} class.
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  @Mock
  private TagRepository tagRepository;

  @Mock EvidenceRepository evidenceRepository;

  @InjectMocks
  private TagService tagService;

  @InjectMocks
  private EvidenceService evidenceService;

  private static final List<Tag> testTags = new ArrayList<>();

  /**
   * setUp list of Tags for testing which will be returned when mocking the repository's method which will return list of Tags.
   */
  @BeforeEach
  void setUp() {
    Tag tag1 = new Tag("Test tag 1");
    Tag tag2 = new Tag("Test tag 2");
    Tag tag3 = new Tag("Test tag 3");
    Tag tag4 = new Tag("Test tag 4");
    testTags.add(tag1);
    testTags.add(tag2);
    testTags.add(tag4);
    testTags.add(tag3);

    /* When evidence has sections for tags, add the tags above to the piece of evidence */
    Evidence evidence1 = new Evidence(0, 1, "testEvidence1", "testEvidence1", new Date(100));
  }

  /**
   * Tests that the getAllTags method returns all tags.
   */
  @Test
  void getAllTags() {
    when(tagRepository.findAll()).thenReturn(testTags);

    List<Tag> actualTags = tagService.getAllTags();

    assertEquals(actualTags.size(), testTags.size());
    verify(tagRepository, times(1)).findAll();
  }

  /**
   * Tests that the getTags(int evidenceId) method returns all tags for a piece of evidence.
   * Implement this when tags are a part of evidences.
   */

  @Test
  void getTags() {
    when(tagRepository.findByEvidenceId(1)).thenReturn(testTags);

    List<Tag> actualTags = tagService.getTags(1);

    assertEquals(4, actualTags.size());
  }


}
