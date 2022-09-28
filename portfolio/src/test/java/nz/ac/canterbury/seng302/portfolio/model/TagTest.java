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

    /**
     * Tests to ensure that the model correctly replaces the "_" and converts them to spaces when getting the name through this method.
     */
    @Test
    void nameSpacedTest() {
        Tag testTag = new Tag("test_tag_name");
        String expectedName = "test tag name";
        assertEquals(expectedName, testTag.getSpacedTagName());
    }
}
