package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.service.ElementService;;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserSortingService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.List;
import java.util.Objects;

/***
 * Controller receive HTTP GET, POST, PUT, DELETE calls for view user page
 */
@Controller
public class ViewUsersController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

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
        UserResponse getUserByIdReply;
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        getUserByIdReply = registerClientService.getUserData(id);
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");


        model.addAttribute("currentUserRole", role);
        model.addAttribute("currentUsername", getUserByIdReply.getUsername());
        model.addAttribute("userId", id);
        model.addAttribute("studentRole", UserRole.STUDENT);
        model.addAttribute("teacherRole", UserRole.TEACHER);
        model.addAttribute("adminRole", UserRole.COURSE_ADMINISTRATOR);

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

    @RequestMapping(value="/add_role", method=RequestMethod.POST)
    public String updateTable(Model model,
                              @AuthenticationPrincipal AuthState principal,
                              @RequestParam(value = "role") String role,
                              @RequestParam(value = "username") String username,
                              @RequestParam(value = "userId") int userId
    ) {

        switch (role) {
            case "student":
                UserRoleChangeResponse roleChangeResponse = userAccountClientService.addRoleToUser(userId, UserRole.STUDENT);
                return "redirect:viewUsers";
        }
        return "viewUsers";
    }

    @RequestMapping(value="/delete_role", method=RequestMethod.POST)
    public String deleteRole(Model model,
                              @AuthenticationPrincipal AuthState principal,
                              @RequestParam(value = "deletedRole") String role,
                              @RequestParam(value = "userId") int userId
    ) {
        UserRoleChangeResponse roleChangeResponse;
        if(Objects.equals(role, "STUDENT")){
            roleChangeResponse = userAccountClientService.deleteRoleFromUser(userId, UserRole.STUDENT);
        } else if(Objects.equals(role, "TEACHER")){
            roleChangeResponse = userAccountClientService.deleteRoleFromUser(userId, UserRole.TEACHER);
        } else {
            roleChangeResponse = userAccountClientService.deleteRoleFromUser(userId, UserRole.COURSE_ADMINISTRATOR);
        }
        System.out.println("reach here");
        return "redirect:/viewUsers";
    }
}
