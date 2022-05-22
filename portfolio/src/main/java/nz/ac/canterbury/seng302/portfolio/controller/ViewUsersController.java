package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.model.UserToGroup;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserSortingService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/***
 * Controller receive HTTP GET, POST, PUT, DELETE calls for view user page
 */
@Controller
public class ViewUsersController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    @Autowired
    private UserSortingService userSortingService;

    @Autowired
    private GroupService groupService;

    private List<UserResponse> userResponseList;

    /***
     * HTTP GET method request handler when url is "/viewUsers"
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param principal Used to get the user's id
     * @return
     */
    @GetMapping("/viewUsers")
    public String showUserTablePage(
            Model model,
            @AuthenticationPrincipal AuthState principal
    ) {
        try {
            Group group = groupService.getGroupById(1);
            System.out.println(group.getGroupId());
            System.out.println(group.getShortName());
            for (Integer id: group.getMemberIds()) {
                System.out.println(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        model.addAttribute("userId", id);

        PaginatedUsersResponse response = userAccountClientService.getAllUsers();
        userResponseList = response.getUsersList();
        model.addAttribute("users", userResponseList);
        UserSorting userSorting;
        try {
            userSorting = userSortingService.getUserSortingById(id);
        } catch (Exception e) {
            userSorting = new UserSorting(id);
        }
        model.addAttribute("userSorting", userSorting);
        return "viewUsers";
    }

    /***
     * POST method request handler when the url is "/viewUsers/saveSort"
     * @param columnIndex an Integer which column is chosen as the base of the sorting
     * @param sortOrder a String between 'asc' or 'desc'. 'asc' means Ascending and 'desc' means Descending
     * @param principal Used to get the user's id
     * @return viewUsers html page
     */
    @RequestMapping(value="/viewUsers/saveSort", method=RequestMethod.POST)
    public String updateSprintRangeErrors(@RequestParam(value="columnIndex") Integer columnIndex,
                                          @RequestParam(value="sortOrder") String sortOrder,
                                          @AuthenticationPrincipal AuthState principal) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        UserSorting userSorting = new UserSorting(id, columnIndex, sortOrder);
        userSortingService.updateUserSorting(userSorting);
        return "viewUsers";
    }

}
