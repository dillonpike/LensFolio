package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


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
    public GroupService groupService;

    @GetMapping("/groupSetting")
    public String groupSetting(@RequestParam(value = "groupId") int groupId,
                               @AuthenticationPrincipal AuthState principal,
                               Model model) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);

        groupService.addGroupDetailToModel(model, groupId);
        return "groupSetting";
    }
}

