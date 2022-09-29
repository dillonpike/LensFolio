package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the category class for evidence.
 */
public class CategoryTest {
    /**
     * Constructor test for the Category class.
     */
    @Test
    void constructorTest() {
        Category testCategory = new Category("testCategory");
        assertEquals("testCategory", testCategory.getCategoryName());
    }
}
