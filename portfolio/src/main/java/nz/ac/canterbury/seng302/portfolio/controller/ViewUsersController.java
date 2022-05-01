package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserSortingService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ViewUsersController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserSortingService userSortingService;

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
        UserResponse getUserByIdReplyHeader;
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        getUserByIdReplyHeader = registerClientService.getUserData(id);
        String fullNameHeader = getUserByIdReplyHeader.getFirstName() + " " + getUserByIdReplyHeader.getMiddleName() + " " + getUserByIdReplyHeader.getLastName();
        model.addAttribute("headerFullName", fullNameHeader);
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
