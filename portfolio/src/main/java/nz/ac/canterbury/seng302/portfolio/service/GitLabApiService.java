package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupSettings;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Contributor;
import org.hibernate.ObjectNotFoundException;
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
    public List<Contributor> getContributors(Integer groupId) throws GitLabApiException {
        GroupSettings groupSettings = groupSettingsService.getGroupSettingsByGroupId(groupId);
        GitLabApi gitLabApi = groupSettings.getGitLabApi();
        return gitLabApi.getRepositoryApi().getContributors(groupSettings.getRepoId());
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
    public List<Commit> getCommits(Integer groupId, String branchName, String userEmail) throws GitLabApiException,ObjectNotFoundException {
        GroupSettings groupSettings = groupSettingsService.getGroupSettingsByGroupId(groupId);
        GitLabApi gitLabApi = groupSettings.getGitLabApi();

        // Gets API to filter by branch if one is given
        List<Commit> commits = branchName == null ? gitLabApi.getCommitsApi().getCommits(groupSettings.getRepoId())
                : gitLabApi.getCommitsApi().getCommits(groupSettings.getRepoId(), branchName, null, null);

        // Filter results by user email if one is given
        return userEmail == null ? commits.stream().sorted((o1, o2)->o2.getCommittedDate().
                compareTo(o1.getCommittedDate())).toList() :
                commits.stream().filter(commit -> Objects.equals(commit.getAuthorEmail(), userEmail)).sorted((o1, o2)->o2.getCommittedDate().
                        compareTo(o1.getCommittedDate())).toList();

    }

    /**
     * Checks if a repository is accessible using the given API key and the repoId.
     * @param repoId the id of the repository
     * @param repoApiKey the API key to use
     * @return true if the repository is accessible, false otherwise
     */
    public boolean checkGitLabToken(int repoId, String repoApiKey) {
        try (GitLabApi gitLabApi = new GitLabApi("https://eng-git.canterbury.ac.nz", repoApiKey)) {
            gitLabApi.getRepositoryApi().getBranches(Integer.toString(repoId));
            return true;
        } catch (GitLabApiException e) {
            return false;
        }
    }
}
