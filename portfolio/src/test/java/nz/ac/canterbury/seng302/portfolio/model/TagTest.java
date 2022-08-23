package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
