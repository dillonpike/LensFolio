package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.HighFivers;
import nz.ac.canterbury.seng302.portfolio.model.Tag;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link EvidenceService} class.
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class EvidenceServiceTest {
    @Mock
    private EvidenceRepository evidenceRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagService tagService;

    @Mock
    private RegisterClientService registerClientService;

    @InjectMocks
    private EvidenceService evidenceService;

    private static final List<Evidence> testEvidences = new ArrayList<>();
    private static final List<Tag> testTags = new ArrayList<>();

    /**
     * setUp list of Evidences for testing which will returned when mocking the repository's method which return list of Evidences.
     */
    @BeforeEach
    void setUp() {
        for (int i = 0; i < 4; i++) {
            Tag tag = new Tag("Test tag " + (i + 1));
            testTags.add(tag);
        }
        Evidence evidence1 = new Evidence(0, 1, "testEvidence1", "testEvidence1", new Date(100));
        Evidence evidence2 = new Evidence(0, 1, "testEvidence2", "testEvidence2", new Date(500));
        Evidence evidence3 = new Evidence(0, 1, "testEvidence3", "testEvidence3", new Date(300));
        Evidence evidence4 = new Evidence(0, 1, "testEvidence4", "testEvidence4", new Date(200));
        for (int i = 0; i < testTags.size(); i++) {
            if (i % 2 == 0) {
                evidence1.addTag(testTags.get(i));
            } else {
                evidence2.addTag(testTags.get(i));
            }
        }
        testEvidences.add(evidence1);
        testEvidences.add(evidence2);
        testEvidences.add(evidence3);
        testEvidences.add(evidence4);
    }

    /**
     * Tests that the getAllEvidences method returns all evidences based on the userId.
     * This test also check that the method return sorted evidence by date. ( evidence with the latest date should be first)
     */
    @Test
    void getEvidences() {
        when(evidenceRepository.findAllByUserId(any(Integer.class))).thenReturn(testEvidences);

        List<Evidence> actualEvidences = evidenceService.getEvidences(1);
        boolean isCorrectlySorted = true;
        for(int i = 1; i < actualEvidences.size(); i++ ) {
            if(actualEvidences.get(i-1).getDate().before(actualEvidences.get(i).getDate())){
                isCorrectlySorted = false;
                break;
            }
        }
        assertTrue(isCorrectlySorted);
        verify(evidenceRepository, times(1)).findAllByUserId(1);
    }

    /**
     * Test that the evidence service can successfully save a new evidence piece to the database.
     */
    @Test
    void testAddEvidence() {
        when(evidenceRepository.save(any(Evidence.class))).thenReturn(testEvidences.get(0));
        assertTrue(evidenceService.addEvidence(testEvidences.get(0)));
    }

    /**
     * Test that the evidence service returns false when an evidence piece is not saved correctly to the database.
     */
    @Test
    void testFailAddEvidence() {
        when(evidenceRepository.save(any(Evidence.class))).thenThrow(new MockitoException("Mockito exception"));
        assertFalse(evidenceService.addEvidence(testEvidences.get(0)));
    }

    /**
     * Tests that no user responses are returned when no users have high fived a piece of evidence.
     */
    @Test
    void testGetHighFiversOfEvidenceWhenNoHighFivers() {
        Evidence testEvidence = new Evidence();
        Set<HighFivers> actualUsers = testEvidence.getHighFivers();
        assertEquals(0, actualUsers.size());
    }

    /**
     * Tests that the removeEvidence(int evidenceId) method removes specific evidence.
     */
    @Test
    void testRemoveEvidence() {
        Evidence evidence = testEvidences.get(0);
        when(evidenceRepository.findById(any(Integer.class))).thenReturn(Optional.of(evidence)).thenReturn(Optional.empty());
        boolean success = evidenceService.removeEvidence(evidence.getEvidenceId());
        assertTrue(success);
    }
    /**
     * Tests that when the method is passed both a valid skill with a valid user attached to the evidence within tag class that
     * the evidence is returned.
     */
    @Test
    void testGetEvidenceWithSkillAndUserWithValidUserAndSkill() {
        int tagId = 1;
        int evidenceId = 1;
        int userId = 1;

        Evidence testEvidence = new Evidence();
        testEvidence.setEvidenceId(evidenceId);
        testEvidence.setUserId(userId);

        Tag validTag = new Tag("Valid_Tag");
        validTag.setTagId(tagId);
        validTag.addEvidence(testEvidence);
        when(tagService.getTag(tagId)).thenReturn(validTag);

        Optional<Evidence> optionalEvidence = Optional.of(testEvidence);
        when(evidenceRepository.findById(evidenceId)).thenReturn(optionalEvidence);

        try {
            List<Evidence> actualEvidences = evidenceService.getEvidencesWithSkillAndUser(tagId, userId);
            ArrayList<Evidence> expectedEvidences = new ArrayList<>();
            expectedEvidences.add(testEvidence);
            assertEquals(expectedEvidences, actualEvidences);
        } catch (NullPointerException e) {
            fail();
        }
    }

    /**
     * Tests that when the method is passed both an invalid skill with a valid/invalid user attached to the evidence within tag class that
     * the evidence is not returned. It should also throw a NullPointerException.
     * When the user is valid as it is attached evidence which is stored in the tag it will still produce a NullPointerException.
     */
    @Test
    void testGetEvidenceWithSkillAndUserWithInvalidUserAndSkill() {
        int tagId = 1;
        int userId = 1;

        when(tagService.getTag(tagId)).thenReturn(null); // Invalid Tag

        try {
            evidenceService.getEvidencesWithSkillAndUser(tagId, userId);
            fail(); // It is expected to throw a NullPointerException.
        } catch (Exception e) {
            if (!(e instanceof NullPointerException)) {
                fail();
            }
        }
    }

    /**
     * Tests that when the method is passed both a valid skill but the user attached to the evidences
     * are not the same as the ones being searched for that an empty list is returned.
     */
    @Test
    void testGetEvidenceWithSkillAndUserWithInvalidUserAndValidSkill() {
        int tagId = 1;
        int evidenceId = 1;
        int userId = 1;

        Evidence testEvidence = new Evidence();
        testEvidence.setEvidenceId(evidenceId);
        testEvidence.setUserId(2); // Not the same as the userId being searched for.

        Tag validTag = new Tag("Valid_Tag");
        validTag.setTagId(tagId);
        validTag.addEvidence(testEvidence);
        when(tagService.getTag(tagId)).thenReturn(validTag);

        try {
            evidenceService.getEvidencesWithSkillAndUser(evidenceId, userId);
            List<Evidence> actualEvidences = evidenceService.getEvidencesWithSkillAndUser(tagId, userId);
            ArrayList<Evidence> expectedEvidences = new ArrayList<>();
            assertEquals(expectedEvidences, actualEvidences);
        } catch (NullPointerException e) {
            fail();
        }
    }

    /**
     * Tests that when searching just for evidences with a certain tag that if the tag is valid all evidences attached are returned.
     */
    @Test
    void testGetEvidenceWithSkillWithValidSkill() {
        int tagId = 1;
        int evidenceId = 1;

        Evidence testEvidence = new Evidence();
        testEvidence.setEvidenceId(evidenceId);

        Tag validTag = new Tag("Valid_Tag");
        validTag.setTagId(tagId);
        validTag.addEvidence(testEvidence);
        when(tagService.getTag(tagId)).thenReturn(validTag);

        try {
            List<Evidence> actualEvidences = evidenceService.getEvidencesWithSkill(tagId);
            ArrayList<Evidence> expectedEvidences = new ArrayList<>();
            expectedEvidences.add(testEvidence);
            assertEquals(expectedEvidences, actualEvidences);
        } catch (NullPointerException e) {
            fail();
        }
    }

    /**
     * Tests that when searching just for evidences with a certain tag that if the tag does not exist that a NullPointerException is thrown.
     */
    @Test
    void testGetEvidenceWithSkillWithInvalidSkill() {
        int tagId = 1;

        when(tagService.getTag(tagId)).thenReturn(null);

        try {
            evidenceService.getEvidencesWithSkill(tagId);
            fail(); // It is expected to throw a NullPointerException.
        } catch (Exception e) {
            if (!(e instanceof NullPointerException)) {
                fail();
            }
        }
    }

    /**
     * Tests that when all evidences with a certain tag are deleted,
     * that the tags no longer exist.
     */
    @Test
    void testRemoveTagsWithNoEvidence() {
        List<Tag> tags = tagRepository.findAll();
        Evidence evidence1 = testEvidences.get(0);
        Evidence evidence2 = testEvidences.get(1);
        evidenceRepository.deleteById(evidence1.getEvidenceId());
        evidenceRepository.deleteById(evidence2.getEvidenceId());
        evidenceService.removeTagsWithNoEvidence();
        List<Tag> tags2 = tagRepository.findAll();
        assertEquals(0, tags2.size());
    }
}
