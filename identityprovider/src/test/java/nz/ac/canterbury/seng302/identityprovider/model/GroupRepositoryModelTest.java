package nz.ac.canterbury.seng302.identityprovider.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains all test for model of Group repository
 */
class GroupRepositoryModelTest {
    /**
     * Tests that the constructor for the Group Repository Model class sets each variable correctly.
     */
    @Test
    void testConstructor() {
        int expectedGroupId = 0;
        String expectedShortName = "Teaching Staff";
        String expectedLongName = "Users Without Group";
        int expectedCourseId = 3;
        Set<Integer> expectedMemberIds = new HashSet<>();
        GroupModel group = new GroupModel(expectedShortName, expectedLongName, expectedCourseId);

        String alias = "expected alias";

        String key = "expectedKey123";

        GroupRepositoryModel groupRepositoryModel = new GroupRepositoryModel(group, alias, key);

        assertEquals(expectedGroupId, groupRepositoryModel.getGroupModel().getGroupId());
        assertEquals(expectedShortName, groupRepositoryModel.getGroupModel().getShortName());
        assertEquals(expectedLongName, groupRepositoryModel.getGroupModel().getLongName());
        assertEquals(expectedCourseId, groupRepositoryModel.getGroupModel().getCourseId());
        assertEquals(expectedMemberIds, groupRepositoryModel.getGroupModel().getMemberIds());
        assertEquals(alias, groupRepositoryModel.getAlias());
        assertEquals(key, groupRepositoryModel.getGitlabApiKey());
    }

}