package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

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

    private static final Integer MEMBERS_WITHOUT_GROUP_ID = 1;

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
     * @param groupId id of group to reload
     * @return Group page
     */
    @RequestMapping("/groups/local")
    public String localRefresh(
            Model model,
            @RequestParam("groupId") int groupId)
    {
        groupService.addGroupDetailToModel(model, groupId);
        groupService.addGroupListToModel(model);
        return "group::table_refresh";
    }

    /**
     * Method tries to add and sve the new group to the database
     * @param group group being added
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param httpServletResponse for adding status codes to
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
            groupService.addGroupDetailToModel(model, response.getNewGroupId());
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return "group::groupCard";
        }

        List<ValidationError> errors = response.getValidationErrorsList();
        groupService.addGroupNameErrorsToModel(model, errors);
        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "fragments/groupModal::groupModalBody";
    }

    /**
     * Submits a request to the identity provider to delete the group with the given id from the database. Adds a
     * variable to the model indicating whether or not this was successful.
     * @param id id of the group to delete
     * @param httpServletResponse for adding status codes to
     */
    @DeleteMapping("/delete-group/{id}")
    @ResponseBody
    public void groupRemove(@PathVariable("id") Integer id,
                              HttpServletResponse httpServletResponse) {
        DeleteGroupResponse response = groupService.deleteGroup(id);
        if (response.getIsSuccess()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Tries to save new data to group with given groupId to the database.
     * @param id id of event edited
     * @param group Group data to be updated
     * @param model model to add attributes to for Thyemeleaf to inject into the HTML
     * @param httpServletResponse for adding status codes to
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
            groupService.addGroupDetailToModel(model, id);
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return "group::groupCard";
        }

        List<ValidationError> errors = response.getValidationErrorsList();
        groupService.addGroupNameErrorsToModel(model, errors);
        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "fragments/groupModal::groupModalBody";
    }

    @PostMapping("/copy-users")
    public String moveUsers(
            @RequestParam("groupId") Integer groupId,
            @RequestParam("userIds") List<Integer> userIds,
            Model model,
            HttpServletResponse httpServletResponse
    ) {
        AddGroupMembersResponse response = groupService.addMemberToGroup(groupId, userIds);
        if (response.getIsSuccess()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            if (Objects.equals(groupId, MEMBERS_WITHOUT_GROUP_ID)) {
                groupService.addGroupListToModel(model);
                return "group::groupList";
            } else {
                groupService.addGroupDetailToModel(model, groupId);
                return "group::groupCard";
            }
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return null;
    }

    @GetMapping("/members-without-a-group")
    public String membersWithoutAGroupCard(
            Model model
    ) {
        groupService.addGroupDetailToModel(model, MEMBERS_WITHOUT_GROUP_ID);
        return "group::groupCard";
    }
}
