package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for group page
 */
@Controller
public class GroupController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    @Autowired
    public GroupService groupService;

    private String updateMessageId = "isUpdateSuccess";


    /**
     * Get method for group page to display group list and group detail
     * @param model  Parameters sent to thymeleaf template to be rendered into HTML
     * @return Group page
     */
    @GetMapping("/groups")
    public String groups(
            Model model,
            @AuthenticationPrincipal AuthState principal
            ) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        return "group";
    }

    /**
     * Method to refresh the group table only
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Group page
     */
    @RequestMapping("/groups/local")
    public String localRefresh(Model model) {
        model.addAttribute("title", "Group1");
        return "group::table_refresh";
    }

    /**
     * Method tries to add and sve the new group to the database
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return redirect user to group page
     */
    @PostMapping("/add-group")
    public String addGroup(
            @ModelAttribute("shortGroupName") String shortName,
            @ModelAttribute("longGroupName") String longName,
            Model model,
            RedirectAttributes rm
    ) {

        CreateGroupResponse response = groupService.createNewGroup(shortName, longName);

        if (response.getIsSuccess()) {
            return "redirect:groups";
        }
        List<ValidationError> errors = response.getValidationErrorsList();
        for (ValidationError error : errors) {
            String errorMessage = error.getErrorText();
            if (errorMessage.contains("Short name")) {
                model.addAttribute("groupShortNameAlertMessage", error.getErrorText());
            }
            if (errorMessage.contains("Long name")) {
                model.addAttribute("groupLongNameAlertMessage", error.getErrorText());
            }
        }

        return "group";
    }

}
