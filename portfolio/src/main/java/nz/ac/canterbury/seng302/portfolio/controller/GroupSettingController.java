package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.model.NotificationGroup;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    private GroupService groupService;

    @Autowired
    private GitLabApiService gitLabApiService;

    @GetMapping("/groupSetting")
    public String groupSetting(@RequestParam(value = "groupId") int groupId,
                               @AuthenticationPrincipal AuthState principal,
                               Model model) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);

        groupService.addGroupDetailToModel(model, groupId);
        return "groupSetting";
    }

    /**
     * Websocket controller to send notification to users to have their pages refreshed when new settings are saved.
     * @param notificationGroup Holds the ID of the group being refreshed.
     * @return The notificationGroup object.
     */
    @MessageMapping("/save-group-settings")
    @SendTo("/webSocketGet/group-settings-saved")
    public NotificationGroup refreshGroupSettings(NotificationGroup notificationGroup) {
        return notificationGroup;
    }

}

