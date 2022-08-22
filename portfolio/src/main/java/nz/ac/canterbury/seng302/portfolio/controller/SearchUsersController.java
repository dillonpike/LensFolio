package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller class for the searching users page for finding evidence of users.
 */
@Controller
public class SearchUsersController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    @Autowired
    private UserSortingService userSortingService;

    /**
     * Get mapping for the search users page.
     * @param model Model of the page.
     * @param request Request of the page.
     * @param principal Principal of the user to get user ID.
     * @return The search users thymeleaf template html.
     */
    @GetMapping("/viewUsersSearch")
    public String showUserSearchTablePage(
            Model model,
            HttpServletRequest request,
            @AuthenticationPrincipal AuthState principal
    ) {
        UserResponse getUserByIdReply;
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        getUserByIdReply = registerClientService.getUserData(id);
        String role = elementService.getUserHighestRole(getUserByIdReply);
        elementService.addDeniedMessage(model, request);
        model.addAttribute("currentUserRole", role);
        model.addAttribute("currentUsername", getUserByIdReply.getUsername());
        model.addAttribute("userId", id);
        PaginatedUsersResponse response = userAccountClientService.getAllUsers();
        List<UserResponse> userResponseList = response.getUsersList();
        model.addAttribute("users", userResponseList);
        UserSorting userSorting;
        try {
            userSorting = userSortingService.getUserSortingById(id);
        } catch (Exception e) {
            userSorting = new UserSorting(id);
        }
        model.addAttribute("userSorting", userSorting);
        return "searchUsers";
    }

    /***
     * POST method request handler when the url is "/viewUsersSearch/saveSort"
     * @param columnIndex an Integer which column is chosen as the base of the sorting
     * @param sortOrder a String between 'asc' or 'desc'. 'asc' means Ascending and 'desc' means Descending
     * @param principal Used to get the user's id
     * @return searchUsers html page
     */
    @PostMapping(value="/viewUsersSearch/saveSort")
    public String saveSort(@RequestParam(value="columnIndex") Integer columnIndex,
                           @RequestParam(value="sortOrder") String sortOrder,
                           @AuthenticationPrincipal AuthState principal) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        UserSorting userSorting = new UserSorting(id, columnIndex, sortOrder);
        userSortingService.updateUserSorting(userSorting);
        return "searchUsers";
    }
}
