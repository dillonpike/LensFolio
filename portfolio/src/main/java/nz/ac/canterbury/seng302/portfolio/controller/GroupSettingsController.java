package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsResponse;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Contributor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
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
    private GroupService groupService;

    @Autowired
    private GroupSettingsService groupSettingsService;

    @Autowired
    private GitLabApiService gitLabApiService;

    /**
     * Method to handle GetMapping request from frontend, and return the group settings page.
     * @param groupId current group id
     * @param model group setting page model
     * @return group settings page
     */
    @RequestMapping("/groupSettings")
    public String groupSettings(@RequestParam(value = "groupId") int groupId,
                               @AuthenticationPrincipal AuthState principal,
                               Model model) throws GitLabApiException {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        model.addAttribute("groupId", groupId);
        // Non-existent group will have a group id of 0 when calling getGroupDetails
        if (0 <= groupService.getGroupDetails(groupId).getGroupId() &&
                groupService.getGroupDetails(groupId).getGroupId() <= 2) {
            return "redirect:/groups";
        }

        groupService.addGroupDetailToModel(model, groupId);
        groupSettingsService.addSettingAttributesToModel(groupId, model);
        addGroupSettingAttributeToModel(model, groupId);

        return "groupSettings";
    }

    /**
     * Method to partial refresh the group repository commits list.
     * @param groupId current group id
     * @param branchName Current project's branches
     * @param userEmail current user's emailal
     * @param model model for group setting page
     * @return repository commits fragment
     */
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
    @PostMapping("/saveGroupSettings")
    public String editGroupSetting(
            Model model,
            @RequestParam(name = "groupLongName") String longName,
            @RequestParam(name = "groupShortName") String shortName,
            @RequestParam(value = "groupId") int groupId,
            @RequestParam(name = "repoName", required = false, defaultValue = "") String repoName,
            @RequestParam(name = "repoID", required = false) int repoId,
            @RequestParam(name = "repoToken", required = false, defaultValue = "") String repoToken,
            @RequestParam(name = "groupSettingsId") int groupSettingsId,
            HttpServletResponse httpServletResponse,
            RedirectAttributes rm
    ) throws GitLabApiException {
        rm.addAttribute("groupId", groupId);
        model.addAttribute("groupId", groupId);
        ModifyGroupDetailsResponse groupResponse = groupService.editGroupDetails(groupId, shortName, longName);

        // First, we check the response from the server to see if edit the group long name is successful
        if (!groupResponse.getIsSuccess()) {
            model.addAttribute("groupLongNameAlertMessage", "error");
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "groupSettings::groupLongNameAlertBanner";
        }

        boolean isSaved = groupSettingsService.isGroupSettingSaved(groupSettingsId, repoId, repoName, repoToken, groupId);
        boolean isConnected = gitLabApiService.checkGitLabToken(repoId, repoToken);
        if(!isConnected) {
            model.addAttribute("groupSettingsAlertMessage", "Invalid Repository Information");
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "groupSettings::groupSettingsAlertBanner";
        }

        if(!isSaved) {
            model.addAttribute("groupSettingsAlertMessage", "Invalid Repository Information");
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "groupSettings::groupSettingsAlertBanner";
        }
        groupService.addGroupDetailToModel(model, groupId);
        groupSettingsService.addSettingAttributesToModel(groupId, model);
        addGroupSettingAttributeToModel(model, groupId);

        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        model.addAttribute("successMessage", "Save changed");
        return "groupSettings::groupSetting";
    }

    /**
     * Method to add model attribute for group setting page, depending on different situations.
     *
     * Situation 1: if current group has set up group setting, add branches and contributors to model
     * Situation 2: if current group has not set up group repository, add isRepoExist attribute to model
     * Situation 3: if current group has set up group setting with connection error, add error message to model
     * @param model model for group setting page
     * @param groupId current group id
     */
    public void addGroupSettingAttributeToModel(Model model, int groupId) {
        try {
            if (groupSettingsService.doesGroupHaveRepo(groupId)){
                List<Contributor> repositoryContributors = gitLabApiService.getContributors(groupId);
                model.addAttribute("repositoryContributors",repositoryContributors);
                List<String> branchesName = gitLabApiService.getBranchNames(groupId);
                model.addAttribute("branchesName", branchesName);
                model.addAttribute("isRepoExist", true);
                model.addAttribute("groupId", groupId);
                model.addAttribute("isConnectionSuccessful", true);
            } else {
                model.addAttribute("isRepoExist", false);
                model.addAttribute("isConnectionSuccessful", true);
            }
        } catch (GitLabApiException exception) {
            //TODO: add error message to model
            model.addAttribute("isConnectionSuccessful", false);
            model.addAttribute("isRepoExist", false);
        }
    }
}


