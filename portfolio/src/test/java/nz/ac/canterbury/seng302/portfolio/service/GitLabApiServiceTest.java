package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupSettingsRepository;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Contributor;
import org.gitlab4j.api.models.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    private GroupSettings testGroupSettings = new GroupSettings(12345, "test repo", "kjbfdsouoih321312ewln", 1);

    @Mock
    private GitLabApi gitLabApi;

    @Mock
    private RepositoryApi repositoryApi;

    @Mock
    private ProjectApi projectApi;

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
            testCommits.add(commit);
        }

    }

    /**
     * Mocking some common functions calls to the GroupSetting and Group SettingsService class.
     * The mocking is done in a function with @BeforeEach annotation instead of @BeforeAll annotation in order to
     * prevent the mocked class (in this case GroupSetting and groupSettingService Class) to be static which would cause
     * a null error when the tests runs.
     */
    @BeforeEach
    void setUpEach() {
        when(groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId())).thenReturn(testGroupSettings);
        when(testGroupSettings.getGitLabApi()).thenReturn(gitLabApi);
    }

    /**
     * Test that the getBranches method returns the list of branch names returned by the GitLab API.
     * The function is expected to return a list of branch names that exists in a repository.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetBranchNames() throws GitLabApiException {
        when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        when(repositoryApi.getBranches(testGroupSettings.getRepoId())).thenReturn(testBranches);

        List<String> actualBranchNames = gitLabApiService.getBranchNames(testGroupSettings.getGroupId());
        List<String> expectedBranchNames = testBranches.stream().map(Branch::getName).toList();
        assertEquals(expectedBranchNames, actualBranchNames);
    }

    /**
     * Test that the getMembers method returns the list of members returned by the GitLab API.
     * The function is expected to return all authors that have contributed to the repo, in this case testMembers list.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetMembers() throws GitLabApiException {
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);
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
    void testGetCommitsNWithBranchNameAndAuthor() throws GitLabApiException {
        Contributor contributor = testContributors.get(4);
        String branchName = testBranches.get(2).getName();
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getCommitsApi().getCommits(testGroupSettings.getRepoId(), branchName, null, null)).thenReturn(testCommits);

        List<Commit> commits = gitLabApiService.getCommits(testGroupSettings.getGroupId(), branchName, contributor.getEmail());
        assertEquals(testCommits.stream().filter(commit -> Objects.equals(commit.getAuthorEmail(), contributor.getEmail())).toList(), commits);
    }
}