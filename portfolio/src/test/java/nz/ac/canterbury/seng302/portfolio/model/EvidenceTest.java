package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the Evidence class and methods.
 */
class EvidenceTest {

    private static Evidence testEvidence;

    /**
     * Sets up the test evidence.
     */
    @BeforeEach
    void setUp() {
        testEvidence = new Evidence(1, 1, "title", "description", new Date());
    }

    /**
     * Tests the constructor of the Evidence class.
     */
    @Test
    void constructorTest() {
        Date testDate = new Date();
        Evidence testEvidence = new Evidence(1, 1, "title", "description", testDate);
        assertEquals(1, testEvidence.getParentProjectId());
        assertEquals(1, (long) testEvidence.getUserId());
        assertEquals("title", testEvidence.getTitle());
        assertEquals("description", testEvidence.getDescription());
        assertEquals(testDate, testEvidence.getDate());
        assertTrue(testEvidence.getTags().isEmpty());
    }

    /**
     * Tests adding tags to evidence.
     */
    @Test
    void addTagsTest() {
        Tag testTag = new Tag("testTag");
        testEvidence.addTag(testTag);
        assertEquals(1, testEvidence.getTags().size());
    }

    /**
     * Tests removing tags from evidence.
     */
    @Test
    void removeTagsTest() {;
        Tag testTag = new Tag("testTag");
        testEvidence.addTag(testTag);
        assertEquals(1, testEvidence.getTags().size());
        testEvidence.removeTag(testTag);
        assertTrue(testEvidence.getTags().isEmpty());
    }

    /**
     * Tests adding weblinks to the set of weblinks.
     */
    @Test
    void addWebLinkTest() {
        WebLink testWebLink = new WebLink("testWebLink");
        testEvidence.addWebLink(testWebLink);
        assertEquals(1, testEvidence.getWebLinks().size());
    }

    /**
     * Tests removing weblinks from the set of weblinks.
     */
    @Test
    void removeWebLinkTest() {
        WebLink testWebLink = new WebLink("testWebLink");
        testEvidence.addWebLink(testWebLink);
        assertEquals(1, testEvidence.getWebLinks().size());
        testEvidence.removeWebLink(testWebLink);
        assertTrue(testEvidence.getWebLinks().isEmpty());
    }
}
