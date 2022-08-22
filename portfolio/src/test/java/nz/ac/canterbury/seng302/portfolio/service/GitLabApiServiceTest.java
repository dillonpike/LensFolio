package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupSettingsRepository;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
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

    private static final List<Member> testMembers = new ArrayList<>();

    private static final List<Commit> testCommits = new ArrayList<>();

    /**
     * Setting up all expected output which then will be used by the test function to compare with the actual result.
     * This function run before all test cases to reduce code duplication
     */
    @BeforeAll
    static void setUp() {
        for (int i=0; i<5; i++) {
            Branch branch = new Branch();
            branch.setName("testBranch" + i);
            testBranches.add(branch);

            Member member = new Member();
            member.setEmail(String.format("testEmail%d@gmail.com", i));
            testMembers.add(member);

            Commit commit = new Commit();
            commit.setAuthorEmail(String.format("testEmail%d@gmail.com", i));
            testCommits.add(commit);
        }
    }

    /**
     * Mocking some common functions calls to the GroupSetting and Group SettingsService class.
     * The mocking should be done in a function with @BeforeEach annotation instead od @BeforeAll annotation in order to
     * prevent the mocked class(in this case GroupSetting and groupSettingService Class) to be static which would cause
     * a null error when the tests runs
     */
    @BeforeEach
    void setUpEach() {
        when(groupSettingsService.getGroupSettingsByGroupId(testGroupSettings.getGroupId())).thenReturn(testGroupSettings);
        when(testGroupSettings.getGitLabApi()).thenReturn(gitLabApi);
    }

    /**
     * Test that the getBranches method returns the list of branch names returned by the GitLab API.
     * Expect the function to return a list of branch's name that exists in a repository
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetBranchNames() throws GitLabApiException {
        when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        when(repositoryApi.getBranches(testGroupSettings.getRepoId())).thenReturn(testBranches);

        List<String> branchNames = gitLabApiService.getBranchNames(testGroupSettings.getGroupId());
        assertEquals(testBranches.stream().map(Branch::getName).toList(), branchNames);
    }

    /**
     * Test that the getMembers method returns the list of members returned by the GitLab API.
     * Expect the function return all author that has contributed to the repo, in this case testMembers list
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetMembers() throws GitLabApiException {
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        when(projectApi.getAllMembers(testGroupSettings.getRepoId())).thenReturn(testMembers);

        List<Member> members = gitLabApiService.getMembers(testGroupSettings.getGroupId());
        assertEquals(testMembers, members);
    }

    /**
     * Test that the getCommits method returns all the commits returned from the GitLab API when not filtering by branch
     * or user.
     * Expect all commits to be returned regardless of user and branch name, in this test we expect all instances in testCommits to be returned
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
     * Test that the getCommits method returns all the commits returned from the GitLab API when filtering by branch name but not with user.
     * Expect all commits returned
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
     * Test that the getCommits method returns all the commits returned from the GitLab API when not filtering by branch
     * or user.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetCommitsNoBranchNameWithAuthor() throws GitLabApiException {
        Member member = testMembers.get(3);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getCommitsApi().getCommits(testGroupSettings.getRepoId())).thenReturn(testCommits);

        List<Commit> commits = gitLabApiService.getCommits(testGroupSettings.getGroupId(), null, member.getEmail());
        assertEquals(testCommits.stream().filter(commit -> Objects.equals(commit.getAuthorEmail(), member.getEmail())).toList(), commits);
    }

    /**
     * Test that the getCommits method returns all the commits returned from the GitLab API when not filtering by branch
     * or user.
     * @throws GitLabApiException if an error occurs when calling the GitLab API
     */
    @Test
    void testGetCommitsNWithBranchNameAndAuthor() throws GitLabApiException {
        Member member = testMembers.get(4);
        String branchName = testBranches.get(2).getName();
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getCommitsApi().getCommits(testGroupSettings.getRepoId(), branchName, null, null)).thenReturn(testCommits);

        List<Commit> commits = gitLabApiService.getCommits(testGroupSettings.getGroupId(), branchName, member.getEmail());
        assertEquals(testCommits.stream().filter(commit -> Objects.equals(commit.getAuthorEmail(), member.getEmail())).toList(), commits);
    }
}