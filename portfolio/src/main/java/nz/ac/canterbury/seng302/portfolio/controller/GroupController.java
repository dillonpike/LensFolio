package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsResponse;
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

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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

    private final String updateMessageId = "isUpdateSuccess";


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

        groupService.addGroupListToModel(model);

        return "group";
    }

    /**
     * Method to refresh the group table only
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Group page
     */
    @RequestMapping("/groups/local")
    public String localRefresh(
            Model model,
            @RequestParam("groupId") int groupId)
    {
        groupService.addGroupDetailToModel(model, groupId);
        return "group::table_refresh";
    }

    /**
     * Method tries to add and sve the new group to the database
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return redirect user to group page
     */
    @PostMapping("/add-group")
    public String addGroup(
            @ModelAttribute("group") Group group,
            Model model,
            HttpServletResponse httpServletResponse
    ) {
        CreateGroupResponse response = groupService.createNewGroup(group.getShortName(), group.getLongName());

        if (response.getIsSuccess()) {
            group.setGroupId(response.getNewGroupId());
            model.addAttribute("group", group);
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return "group::groupCard";
        }

        List<ValidationError> errors = response.getValidationErrorsList();
        for (ValidationError error : errors) {
            String errorMessage = error.getErrorText();
            if (errorMessage.contains("Short")) {
                model.addAttribute("groupShortNameAlertMessage", error.getErrorText());
            }
            if (errorMessage.contains("Long")) {
                model.addAttribute("groupLongNameAlertMessage", error.getErrorText());
            }
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "fragments/groupModal::groupModalBody";
    }

    /**
     * Submits a request to the identity provider to delete the group with the given id from the database. Adds a
     * variable to the model indicating whether or not this was successful.
     * @param id id of the group to delete
     * @param rd injects the variable indicating success to the HTML
     * @return redirect user to group page
     */
    @GetMapping("/delete-group/{id}")
    public String groupRemove(@PathVariable("id") Integer id, RedirectAttributes rd) {
        DeleteGroupResponse response = groupService.deleteGroup(id);
        rd.addAttribute(updateMessageId, response.getIsSuccess());
        return "redirect:/groups";
    }

    /**
     * Tries to save new data to group with given groupId to the database.
     * @param id        id of event edited
     * @param group     Group data to be updated
     * @throws IllegalArgumentException if sprint cannot be found from the given ID or if it cannot be saved.
     */
    @PostMapping("/edit-group/{id}")
    public String groupEdit(
            @PathVariable("id") Integer id,
            @ModelAttribute("group") Group group,
            Model model,
            HttpServletResponse httpServletResponse
    ) throws IllegalArgumentException {
        ModifyGroupDetailsResponse response = groupService.editGroupDetails(id, group.getShortName(), group.getLongName());

        if (response.getIsSuccess()) {
            group.setGroupId(id);
            model.addAttribute("group", group);
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return "group::groupCard";
        }

        List<ValidationError> errors = response.getValidationErrorsList();
        for (ValidationError error : errors) {
            String errorMessage = error.getErrorText();
            if (errorMessage.contains("Short")) {
                model.addAttribute("groupShortNameAlertMessage", error.getErrorText());
            }
            if (errorMessage.contains("Long")) {
                model.addAttribute("groupLongNameAlertMessage", error.getErrorText());
            }
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "fragments/groupModal::groupModalBody";
    }

}
