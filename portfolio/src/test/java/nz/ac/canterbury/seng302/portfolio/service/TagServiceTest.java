package nz.ac.canterbury.seng302.portfolio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

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

    @Mock
    EvidenceRepository evidenceRepository;

    @InjectMocks
    private TagService tagService;

    @InjectMocks
    private EvidenceService evidenceService;

    private static List<Tag> testTags = new ArrayList<>();
    private static List<Evidence> testEvidences = new ArrayList<>();

    /**
     * setUp list of Tags for testing which will be returned when mocking the repository's method which will return list of Tags.
     */
    @BeforeEach
    void setUp() {
        testTags = new ArrayList<>();
        testEvidences = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Tag tag = new Tag("Test tag " + (i + 1));
            testTags.add(tag);
        }

        /* When evidence has sections for tags, add the tags above to the piece of evidence */
        Evidence evidence1 = new Evidence(0, 1, "testEvidence1", "testEvidence1", new Date(100));
        Evidence evidence2 = new Evidence(0, 1, "testEvidence2", "testEvidence2", new Date(100));
        for (int i = 0; i < testTags.size(); i++) {
            if (i % 2 == 0) {
              evidence1.addTag(testTags.get(i));
            } else {
              evidence2.addTag(testTags.get(i));
            }
        }
        testEvidences.add(evidence1);
        testEvidences.add(evidence2);
    }

    /**
     * Tests that the getAllTags method returns all tags.
     */
    @Test
    void testGetAllTags() {
        when(tagRepository.findAll()).thenReturn(new ArrayList<>(testTags));

        List<Tag> actualTags = tagService.getAllTags();

        assertEquals(actualTags.size(), testTags.size());
        verify(tagRepository, times(1)).findAll();
    }

    /**
     * Tests that the getTagsFromUserId method returns all the tags attached to the user's pieces of evidence.
     */
    @Test
    void testGetTagsFromUserId() {
        int userId = 5;
        when(evidenceRepository.findAllByUserId(userId)).thenReturn(testEvidences);

        List<Tag> actualTags = tagService.getTagsFromUserId(userId);

        assertEquals(new HashSet<>(testTags), new HashSet<>(actualTags));
    }

    /**
     * Tests that the getTagsFromUserId method returns an empty list when the user has no pieces of evidence.
     */
    @Test
    void testGetTagsFromUserIdWhenNoEvidence() {
        int userId = 5;
        when(evidenceRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        List<Tag> actualTags = tagService.getTagsFromUserId(userId);

        assertEquals(new ArrayList<>(), actualTags);
    }


    /**
     * Tests that the removeTag(int tagId) method removes a specific tag.
     */
    @Test
    void testRemoveTag() {
        Tag tag = testTags.get(1);
        int tagId = tag.getTagId();
        doReturn(Optional.of(tag)).when(tagRepository).findById(tagId);
        doNothing().when(tagRepository).deleteById(tagId);
        boolean success = tagService.removeTag(tagId);
        assertTrue(success);
        verify(tagRepository).deleteById(tagId);
    }

    /**
     * Tests getting the tags of a user (blue sky scenario).
     */
    @Test
    void testGetTagsOfUser() {
        int userId = 5;
        when(evidenceRepository.findAllByUserId(userId)).thenReturn(testEvidences);

        Set<Tag> actualTags = tagService.getTagsOfUser(userId);

        assertEquals(new HashSet<>(testTags), new HashSet<>(actualTags));
    }

    /**
     * Tests getting the tags of a user when the user has no evidence.
     */
    @Test
    void testGetTagsOfUserWhenUserHasNoEvidenceOrTags() {
        int userId = 5;
        when(evidenceRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        Set<Tag> actualTags = tagService.getTagsOfUser(userId);

        assertEquals(new HashSet<>(), new HashSet<>(actualTags));
    }

    /**
     * Tests getting the tags of a user sorted by tag name, with the first tag being "No_skills" (blue sky scenario).
     */
    @Test
    void testGetTagsByUserSortedList() {
        int userId = 5;
        when(evidenceRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>(testEvidences));

        List<Tag> actualTags = tagService.getTagsByUserSortedList(userId);

        List<Tag> expectedTags = new ArrayList<>(testTags);
        expectedTags.sort(Comparator.comparing(Tag::getTagName));
        Tag noSkill = new Tag("No_skills");
        noSkill.setTagId(-1);
        expectedTags.add(0, noSkill);

        assertEquals(expectedTags, actualTags);
        assertEquals(testTags.size() + 1, actualTags.size());
        assertEquals("No_skills", actualTags.get(0).getTagName());
    }

    /**
     * Tests getting the tags of a user when they have no tags/evidence, with the first tag being "No_skills".
     */
    @Test
    void testGetTagsByUserSortedListWhenUserHasNoEvidenceOrTags() {
        int userId = 5;
        when(evidenceRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        List<Tag> actualTags = tagService.getTagsByUserSortedList(userId);

        List<Tag> expectedTags = new ArrayList<>();
        Tag noSkill = new Tag("No_skills");
        noSkill.setTagId(-1);
        expectedTags.add(0, noSkill);

        assertEquals(expectedTags, actualTags);
        assertEquals(1, actualTags.size());
        assertEquals("No_skills", actualTags.get(0).getTagName());
    }

    /**
     * Tests getting all tags sorted by tag name, with the first tag being "No_skills" (blue sky scenario).
     */
    @Test
    void testGetTagsSortedList() {
        when(tagRepository.findAll()).thenReturn(new ArrayList<>(testTags));

        List<Tag> actualTags = tagService.getTagsSortedList();

        List<Tag> expectedTags = new ArrayList<>(testTags);
        expectedTags.sort(Comparator.comparing(Tag::getTagName));
        Tag noSkill = new Tag("No_skills");
        expectedTags.add(0, noSkill);

        assertEquals(expectedTags, actualTags);
        assertEquals(testTags.size() + 1, actualTags.size());
        assertEquals("No_skills", actualTags.get(0).getTagName());
    }

    /**
     * Tests getting all tags sorted by tag name, with the first tag being "No_skills", when no tags exist.
     */
    @Test
    void testGetTagsSortedListWhenNoTagsExist() {
        when(tagRepository.findAll()).thenReturn(new ArrayList<>());

        List<Tag> actualTags = tagService.getTagsSortedList();

        List<Tag> expectedTags = new ArrayList<>();
        Tag noSkill = new Tag("No_skills");
        expectedTags.add(0, noSkill);

        assertEquals(expectedTags, actualTags);
        assertEquals(1, actualTags.size());
        assertEquals("No_skills", actualTags.get(0).getTagName());
    }

    /**
     * Tests that the getTagsOfEvidence method returns a set of tags when given a valid evidence id.
     */
    @Test
    void testGetTagsOfEvidence() {
        int evidenceId = 1;
        when(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(testEvidences.get(0)));
        Set<Tag> actualTags = tagService.getTagsOfEvidence(evidenceId);
        assertEquals(new HashSet<>(testEvidences.get(0).getTags()), new HashSet<>(actualTags));
    }

    /**
     * Tests the getTag method returns the tag from the findById JPA method.
     */
    @Test
    void testGetTag() {
        Tag tag = testTags.get(1);
        int tagId = tag.getTagId();
        doReturn(Optional.of(tag)).when(tagRepository).findById(tagId);
        Tag actualTag = tagService.getTag(tagId);
        assertEquals(tag, actualTag);
        verify(tagRepository).findById(tagId);
    }

    /**
     * Tests the getTagByNameIgnoreCase method returns the tag from the findByTagNameIgnoreCase JPA method.
     */
    @Test
    void testGetTagByNameIgnoreCase() {
        Tag tag = testTags.get(1);
        String tagName = tag.getTagName();
        doReturn(List.of(tag)).when(tagRepository).findByTagNameIgnoreCase(tagName);
        Tag actualTag = tagService.getTagByNameIgnoreCase(tagName);
        assertEquals(tag, actualTag);
        verify(tagRepository).findByTagNameIgnoreCase(tagName);
    }
}
