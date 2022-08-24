package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
    private GitLabApiService gitLabApiService;

    @GetMapping("/groupSettings")
    public String groupSettings(@RequestParam(value = "groupId") int groupId,
                               @AuthenticationPrincipal AuthState principal,
                               Model model) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        if (0 <= groupService.getGroupDetails(groupId).getGroupId() &&
                groupService.getGroupDetails(groupId).getGroupId() <= 2) {
            return "redirect:/groups";
        }
        groupService.addGroupDetailToModel(model, groupId);
        return "groupSettings";
    }
}

