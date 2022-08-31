package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Methods for getting information from a repository using the GitLab API.
 */
@Service
public class GitLabApiService {

    @Autowired
    private GroupSettingsService groupSettingsService;

    /**
     * Returns a list of branch names for the repository linked to the group.
     * @param groupId the id of the group
     * @return list of branch names for the repository linked to the group
     * @throws GitLabApiException if any exception occurs communicating with the GitLab API
     */
    public List<String> getBranchNames(Integer groupId) throws GitLabApiException {
        GroupSettings groupSettings = groupSettingsService.getGroupSettingsByGroupId(groupId);
        GitLabApi gitLabApi = groupSettings.getGitLabApi();
        return gitLabApi.getRepositoryApi().getBranches(groupSettings.getRepoId())
                .stream().map(Branch::getName).toList();
    }

    /**
     * Returns a list of members of the repository linked to the group.
     * @param groupId the id of the group
     * @return list of users in the repository linked to the group
     * @throws GitLabApiException if any exception occurs communicating with the GitLab API
     */
    public List<Member> getMembers(Integer groupId) throws GitLabApiException {
        GroupSettings groupSettings = groupSettingsService.getGroupSettingsByGroupId(groupId);
        GitLabApi gitLabApi = groupSettings.getGitLabApi();
        return gitLabApi.getProjectApi().getAllMembers(groupSettings.getRepoId());
    }

    /**
     * Returns a list of commits in the repository linked to the group.
     * Commits can be filtered by branch and user if provided, otherwise pass in null for either or all of them.
     * @param groupId the id of the group
     * @param branchName the name of the branch
     * @param userEmail the email of the user
     * @return list of commits from the given branch in the repository linked to the group
     * @throws GitLabApiException if any exception occurs communicating with the GitLab API
     */
    public List<Commit> getCommits(Integer groupId, String branchName, String userEmail) throws GitLabApiException {
        GroupSettings groupSettings = groupSettingsService.getGroupSettingsByGroupId(groupId);
        GitLabApi gitLabApi = groupSettings.getGitLabApi();

        // Gets API to filter by branch if one is given
        List<Commit> commits = branchName == null ? gitLabApi.getCommitsApi().getCommits(groupSettings.getRepoId())
                : gitLabApi.getCommitsApi().getCommits(groupSettings.getRepoId(), branchName, null, null);

        // Filter results by user email if one is given
        return userEmail == null ? commits :
                commits.stream().filter(commit -> Objects.equals(commit.getAuthorEmail(), userEmail)).toList();
    }
}
