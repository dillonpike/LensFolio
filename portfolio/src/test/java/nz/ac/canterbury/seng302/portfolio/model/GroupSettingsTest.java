package nz.ac.canterbury.seng302.portfolio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.util.Date;
import org.junit.jupiter.api.Test;
/**
 * Unit tests for the Group Settings class.
 */
class GroupSettingsTest {

    /**
     * Tests that the constructor for the GroupSettings class sets each variable correctly.
     */
    @Test
    void testConstructor() {
      int expectedRepoId = 0;
      int expectedGroupId = 0;
      String expectedRepoName = "Test Repo";
      String expectedRepoAPIKey = "TEST123";

      GroupSettings groupSettings = new GroupSettings(expectedRepoId, expectedRepoName,
          expectedRepoAPIKey, expectedGroupId);

      assertEquals(expectedRepoId, groupSettings.getRepoId());
      assertEquals(expectedRepoName, groupSettings.getRepoName());
      assertEquals(expectedRepoAPIKey, groupSettings.getRepoApiKey());
      assertEquals(expectedGroupId, groupSettings.getGroupId());
    }
}
