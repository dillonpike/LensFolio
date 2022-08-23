package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Contributor;
import org.gitlab4j.api.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * Controller for group setting page.
 */
@Controller
public class GroupSettingController {

    @Autowired
    private ElementService elementService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private GitLabApiService gitLabApiService;

    @GetMapping("/groupSetting")
    public String groupSetting(@RequestParam(value = "groupId") int groupId,
                               @AuthenticationPrincipal AuthState principal,
                               Model model) throws GitLabApiException {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        groupService.addGroupDetailToModel(model, groupId);
        List<Contributor> repositoryContributors = gitLabApiService.getContributors(groupId);
        model.addAttribute("repositoryContributors",repositoryContributors);
        List<String> branchesName = gitLabApiService.getBranchNames(groupId);
        model.addAttribute("branchesName", branchesName);
        return "groupSetting";
    }

    @GetMapping("/repository-commits")
    public String getRepositoryCommits(@RequestParam(value = "groupId") int groupId,
                                       @RequestParam(value = "branchName") String branchName,
                                       @RequestParam(value = "userEmail") String userEmail,
                                       @AuthenticationPrincipal AuthState principal,
                               Model model) {
        try{
            String branchRequestName = null;
            String userRequestEmail = null;
            if(!branchName.equals("All Branches")){
                branchRequestName = branchName;
            }
            if(!userEmail.equals("All Users")){
                userRequestEmail = userEmail;
            }
            List<Commit> allCommit = gitLabApiService.getCommits(groupId, branchRequestName, userRequestEmail);
            model.addAttribute("commitList", allCommit);
            return "groupSetting::commitsListRefresh";
        } catch (GitLabApiException e) {
            return "groupSetting";
        }
    }

    /**
     * POST method for group setting page to update group long name,
     * Repository name, Id, and Access Token
     * @return group setting page
     */
    @PostMapping("/saveGroupSetting")
    public String editGroupSetting(
            @RequestParam(name = "longName") String longName,
            @RequestParam(name = "repoName") String repoName,
            @RequestParam(name = "repoID") int repoId,
            @RequestParam(name = "repoToken") String repoToken,
            @RequestParam(name = "groupId") int groupId
    ) {
        return "groupSetting";
    }
}

