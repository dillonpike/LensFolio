package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.service.*;;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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
    private PermissionService permissionService;

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
    public String saveSort(@RequestParam(value="columnIndex") Integer columnIndex,
                           @RequestParam(value="sortOrder") String sortOrder,
                           @AuthenticationPrincipal AuthState principal) {
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        UserSorting userSorting = new UserSorting(id, columnIndex, sortOrder);
        userSortingService.updateUserSorting(userSorting);
        return "viewUsers";
    }

    /***
     * Post method request handler for adding user role
     * @param role The role object indicating the added role(request)
     * @param userId The current user id of the edited user
     * @return list of users page(html)
     */
    @RequestMapping(value="/add_role", method=RequestMethod.POST)
    public String addRole(Model model,
                              RedirectAttributes rm,
                              @RequestParam(value = "role") String role,
                              @RequestParam(value = "userId") int userId,
                              @AuthenticationPrincipal AuthState principal
                              ) {

        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);

        // Check if current user's operation is valid, if invalid, access denied error is displayed to user
        if (permissionService.isValidToModifyRole(role, id)) {
            if (role.equals("student")) {
                UserRoleChangeResponse roleChangeResponse = userAccountClientService.addRoleToUser(userId, UserRole.STUDENT);
            } else if (role.equals("teacher")) {
                UserRoleChangeResponse roleChangeResponse = userAccountClientService.addRoleToUser(userId, UserRole.TEACHER);
            } else {
                UserRoleChangeResponse roleChangeResponse = userAccountClientService.addRoleToUser(userId, UserRole.COURSE_ADMINISTRATOR);
            }
            return "redirect:viewUsers";
        }
        rm.addFlashAttribute("isAccessDenied", true);
        return "redirect:viewUsers";
    }


    /***
     * POST method request handler when the url is "/delete_role"
     * It checks what role that need to be deleted from a user(indicated by the user id) and call delete role service
     * @param role a String object indicating the user role that will be deleted
     * @param userId an Integer indicating the user id of a user that a role will be deleted from
     * @return viewUsers html page. If delete role failed create error Message model which contains the error message
     */
    @RequestMapping(value="/delete_role", method=RequestMethod.POST)
    public String deleteRole(Model model,
                              @RequestParam(value = "deletedRole") String role,
                              @RequestParam(value = "userId") int userId,
                             @AuthenticationPrincipal AuthState principal
                             ) {

        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);

        // Check if current user's operation is valid, if invalid, access denied error is displayed to user
        if (permissionService.isValidToModifyRole(role, id)) {
            UserRoleChangeResponse roleChangeResponse;
            if (Objects.equals(role, "STUDENT")) {
                roleChangeResponse = userAccountClientService.deleteRoleFromUser(userId, UserRole.STUDENT);
            } else if (Objects.equals(role, "TEACHER")) {
                roleChangeResponse = userAccountClientService.deleteRoleFromUser(userId, UserRole.TEACHER);
            } else {
                roleChangeResponse = userAccountClientService.deleteRoleFromUser(userId, UserRole.COURSE_ADMINISTRATOR);
            }
            if (roleChangeResponse.getIsSuccess()) {
                return "redirect:viewUsers";
            } else {
                model.addAttribute("errorMessage", "Error deleting user");
                return "redirect:error";
            }
        }
//        rm.addFlashAttribute("isAccessDenied", true);
        return "redirect:viewUsers";
    }


}
