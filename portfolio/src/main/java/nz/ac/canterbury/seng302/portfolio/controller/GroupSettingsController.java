package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Contributor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


/**
 * Controller for group setting page.
 */
@Controller
public class GroupSettingsController {

    @Autowired
    private ElementService elementService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private GroupSettingsService groupSettingsService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private GitLabApiService gitLabApiService;

    @GetMapping("/groupSettings")
    public String groupSettings(@RequestParam(value = "groupId") int groupId,
                               @AuthenticationPrincipal AuthState principal,
                               Model model) throws GitLabApiException {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);

        // Non-existent group will have a group id of 0 when calling getGroupDetails
        if (0 <= groupService.getGroupDetails(groupId).getGroupId() &&
                groupService.getGroupDetails(groupId).getGroupId() <= 2) {
            return "redirect:/groups";
        }

        groupService.addGroupDetailToModel(model, groupId);
        try {
            List<Contributor> repositoryContributors = gitLabApiService.getContributors(groupId);
            model.addAttribute("repositoryContributors",repositoryContributors);
            List<String> branchesName = gitLabApiService.getBranchNames(groupId);
            model.addAttribute("branchesName", branchesName);
            model.addAttribute("isRepoExist", true);
            model.addAttribute("groupId", groupId);
        } catch (ObjectNotFoundException e) {
            model.addAttribute("isRepoExist", false);
        }

        return "groupSettings";
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
            return "groupSettings::commitsListRefresh";
        } catch (GitLabApiException | ObjectNotFoundException e) {
            return "groupSettings::commitsListRefresh";
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
            @RequestParam(value = "groupId") int groupId,
            @RequestParam(name = "repoName") String repoName,
            @RequestParam(name = "repoID") int repoId,
            @RequestParam(name = "repoToken") String repoToken,
            RedirectAttributes rm
    ) {
//        groupSettingsService.saveGroupSettings()
        rm.addAttribute("groupId", groupId);
        return "redirect:groupSetting";
    }
}

