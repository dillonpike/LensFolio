package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        model.addAttribute("studentRole", UserRole.STUDENT);
        model.addAttribute("teacherRole", UserRole.TEACHER);
        model.addAttribute("adminRole", UserRole.COURSE_ADMINISTRATOR);
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
     * @return viewUsers html page
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
