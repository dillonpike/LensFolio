package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the WebLink class for evidence.
 */
class WebLinkTest {

    /**
     * Constructor test for the WebLink class.
     */
    @Test
    void constructorTest() {
        WebLink testWebLink = new WebLink("testWebLink");
        assertEquals("testWebLink", testWebLink.getUrl());
    }
}
