package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Contributor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link GitLabApiService} class.
 * Mocks the GroupSettingsService class and the GitLabAPI.
 */
@ExtendWith(MockitoExtension.class)
class GitLabApiServiceTest {

    @Mock
    private GroupSettingsService groupSettingsService;

    @InjectMocks
    private GitLabApiService gitLabApiService;

    @Spy
    private GroupSettings testGroupSettings = new GroupSettings(12345, "test repo", "kjbfdsouoih321312ewln", 1, "https://eng-git.canterbury.ac.nz");

    @Mock
    private GitLabApi gitLabApi;

    @Mock
    private RepositoryApi repositoryApi;

    @Mock
    private CommitsApi commitsApi;

    private static final List<Branch> testBranches = new ArrayList<>();

    private static final List<Contributor> testContributors = new ArrayList<>();

    private static final List<Commit> testCommits = new ArrayList<>();

    /**
     * Setting up all expected output which then will be used by the test function to compare with the actual result.
     * Runs before all tests as the information only needs to be set once.
     */
    @BeforeAll
    static void setUp() {
        for (int i=0; i<5; i++) {
            Branch branch = new Branch();
            branch.setName("testBranch" + i);
            testBranches.add(branch);

            Contributor contributor = new Contributor();
            contributor.setEmail(String.format("testEmail%d@gmail.com", i));
            testContributors.add(contributor);

            Commit commit = new Commit();
            commit.setAuthorEmail(String.format("testEmail%d@gmail.com", i));
            commit.setCommittedDate(new Date(i * -1000));
            testCommits.add(commit);
        }
    }

    /**
     * Test that the getBranches method returns the list of branch names returned by the GitLab API.
     * The function is expected to return a list of branch names that exists in a repository.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetBranchNames() throws GitLabApiException {
        when(groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId())).thenReturn(testGroupSettings);
        when(testGroupSettings.getGitLabApi()).thenReturn(gitLabApi);
        when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        when(repositoryApi.getBranches(testGroupSettings.getRepoId())).thenReturn(testBranches);

        List<String> actualBranchNames = gitLabApiService.getBranchNames(testGroupSettings.getGroupId());
        List<String> expectedBranchNames = testBranches.stream().map(Branch::getName).toList();
        assertEquals(expectedBranchNames, actualBranchNames);
    }

    /**
     * Test that the getContributors method returns the list of members returned by the GitLab API.
     * The function is expected to return all authors that have contributed to the repo, in this case testContributors list.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetContributors() throws GitLabApiException {
        when(groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId())).thenReturn(testGroupSettings);
        when(testGroupSettings.getGitLabApi()).thenReturn(gitLabApi);
        when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        when(repositoryApi.getContributors(testGroupSettings.getRepoId())).thenReturn(testContributors);

        List<Contributor> members = gitLabApiService.getContributors(testGroupSettings.getGroupId());
        assertEquals(testContributors, members);
    }

    /**
     * Test that the getCommits method returns all the commits returned from the GitLab API when not filtering by branch
     * or user.
     * The function is expected to return all commits regardless of user and branch name,
     * in this test we expect all commits in the testCommits list to be returned.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetCommitsNoBranchNameNoAuthor() throws GitLabApiException {
        when(groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId())).thenReturn(testGroupSettings);
        when(testGroupSettings.getGitLabApi()).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getCommitsApi().getCommits(testGroupSettings.getRepoId())).thenReturn(testCommits);

        List<Commit> commits = gitLabApiService.getCommits(testGroupSettings.getGroupId(), null, null);
        assertEquals(testCommits, commits);
    }

    /**
     * Test that the getCommits method returns all the commits returned from the GitLab API when filtering by branch
     * but not by user.
     * Branch filtering is done by the API, which is being mocked, so the function is expected to return all commits in
     * the testCommits list.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetCommitsWithBranchNameNoAuthor() throws GitLabApiException {
        String branchName = testBranches.get(0).getName();
        when(groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId())).thenReturn(testGroupSettings);
        when(testGroupSettings.getGitLabApi()).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getCommitsApi().getCommits(testGroupSettings.getRepoId(), branchName, null, null)).thenReturn(testCommits);

        List<Commit> commits = gitLabApiService.getCommits(testGroupSettings.getGroupId(), branchName, null);
        assertEquals(testCommits, commits);
    }

    /**
     * Test that the getCommits method returns all the commits returned from the GitLab API when filtering by user but
     * not by branch.
     * The function is expected to return all commits in the testCommits list with a matching author.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetCommitsNoBranchNameWithAuthor() throws GitLabApiException {
        Contributor contributor = testContributors.get(3);
        when(groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId())).thenReturn(testGroupSettings);
        when(testGroupSettings.getGitLabApi()).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getCommitsApi().getCommits(testGroupSettings.getRepoId())).thenReturn(testCommits);

        List<Commit> commits = gitLabApiService.getCommits(testGroupSettings.getGroupId(), null, contributor.getEmail());
        assertEquals(testCommits.stream().filter(commit -> Objects.equals(commit.getAuthorEmail(), contributor.getEmail())).toList(), commits);
    }

    /**
     * Test that the getCommits method returns all the commits returned from the GitLab API when filtering by branch
     * and user.
     * The function is expected to return all commits in the testCommits list with a matching author.
     * Branch filtering is done by the API, which is being mocked, so the branch name doesn't affect the output
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetCommitsWithBranchNameAndAuthor() throws GitLabApiException {
        Contributor contributor = testContributors.get(4);
        String branchName = testBranches.get(2).getName();
        when(groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId())).thenReturn(testGroupSettings);
        when(testGroupSettings.getGitLabApi()).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getCommitsApi().getCommits(testGroupSettings.getRepoId(), branchName, null, null)).thenReturn(testCommits);

        List<Commit> commits = gitLabApiService.getCommits(testGroupSettings.getGroupId(), branchName, contributor.getEmail());
        assertEquals(testCommits.stream().filter(commit -> Objects.equals(commit.getAuthorEmail(), contributor.getEmail())).toList(), commits);
    }

    /**
     * Checks that the checkGitLabToken method returns true when the repo id and token can be used to get information
     * from the GitLab API.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testCheckGitLabTokenValid() throws GitLabApiException {
        int repoId = 12345;
        MockedConstruction<GitLabApi> mockedConstruction = mockConstruction(GitLabApi.class, (mock, context) ->
                when(mock.getRepositoryApi()).thenReturn(repositoryApi));
        when(repositoryApi.getBranches(Integer.toString(repoId))).thenReturn(null);

        assertTrue(gitLabApiService.checkGitLabToken(repoId, "testToken", "https://eng-git.canterbury.ac.nz"));
        mockedConstruction.close();
    }

    /**
     * Checks that the checkGitLabToken method returns false when the repo id and token cannot be used to get
     * information from the GitLab API.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testCheckGitLabTokenInvalid() throws GitLabApiException {
        int repoId = 12345;
        MockedConstruction<GitLabApi> mockedConstruction = mockConstruction(GitLabApi.class, (mock, context) ->
                when(mock.getRepositoryApi()).thenReturn(repositoryApi));
        when(repositoryApi.getBranches(Integer.toString(repoId))).thenThrow(GitLabApiException.class);

        assertFalse(gitLabApiService.checkGitLabToken(repoId, "testToken", "https://eng-git.canterbury.ac.nz"));
        mockedConstruction.close();
    }

}