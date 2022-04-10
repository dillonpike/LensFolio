package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
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
     * @param principal
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
        System.out.println(userResponseList);
        model.addAttribute("users", userResponseList);


        return "viewUsers";
    }

    @RequestMapping(value="/viewUsers/saveSort", method=RequestMethod.POST)
    public String updateSprintRangeErrors(@RequestParam(value="columnIndex") Integer columnIndex,
                                          @RequestParam(value="order") String order,
                                          @AuthenticationPrincipal AuthState principal) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        UserSorting userSorting = new UserSorting(id, columnIndex, order);
        UserSorting sortUpdate = userSortingService.updateUserSorting(userSorting);
        return "viewUsers";
    }

}
