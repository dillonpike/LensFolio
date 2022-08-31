package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the tag class for evidence.
 */
class TagTest {

    /**
     * Constructor test for the Tag class.
     */
    @Test
    void constructorTest() {
        Tag testTag = new Tag("testTag");
        assertEquals("testTag", testTag.getTagName());
    }
}
