package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.NotificationMessage;
import nz.ac.canterbury.seng302.portfolio.model.NotificationResponse;
import nz.ac.canterbury.seng302.portfolio.model.UserSorting;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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

    public static final String REDIRECT_TO_VIEW_USERS = "redirect:viewUsers";


    /***
     * HTTP GET method request handler when url is "/viewUsers"
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param principal Used to get the user's id
     * @return viewUsers user page
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
        elementService.addUsersToModel(model, id);
        return "viewUsers";
    }

    /***
     * POST method request handler when the url is "/viewUsers/saveSort"
     * @param columnIndex an Integer which column is chosen as the base of the sorting
     * @param sortOrder a String between 'asc' or 'desc'. 'asc' means Ascending and 'desc' means Descending
     * @param principal Used to get the user's id
     * @return viewUsers html page
     */
    @PostMapping(value="/viewUsers/saveSort")
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
    @PostMapping(value="/add_role")
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
                userAccountClientService.addRoleToUser(userId, UserRole.STUDENT);
            } else if (role.equals("teacher")) {
                userAccountClientService.addRoleToUser(userId, UserRole.TEACHER);
            } else {
                userAccountClientService.addRoleToUser(userId, UserRole.COURSE_ADMINISTRATOR);
            }
        } else {
            rm.addFlashAttribute("isAccessDenied", true);
        }
        return REDIRECT_TO_VIEW_USERS;
    }


    /***
     * POST method request handler when the url is "/delete_role"
     * It checks what role that need to be deleted from a user(indicated by the user id) and call delete role service
     * @param role a String object indicating the user role that will be deleted
     * @param userId an Integer indicating the user id of a user that a role will be deleted from
     * @return viewUsers html page. If delete role failed create error Message model which contains the error message
     */
    @PostMapping(value="/delete_role")
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
                return REDIRECT_TO_VIEW_USERS;
            } else {
                model.addAttribute("errorMessage", "Error deleting user");
                return "redirect:error";
            }
        }
        return REDIRECT_TO_VIEW_USERS;
    }

    /**
     * This method used to mainly reload the calendar page when an artefact is being edited or deleted on the project details
     * @param message this parameter, even though it is not used, is necessary to exist in order to send the request to websocket
     */
    @MessageMapping("/add-roles")
    @SendTo("/webSocketGet/add-roles")
    public NotificationResponse addRoles(NotificationMessage message) {
        return new NotificationResponse(message.getUsername());
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This is triggered when a user has been removed student role
     * @param message Information about removing student role
     * @return Returns the message given.
     */
    @MessageMapping("/delete-student-role")
    @SendTo("/webSocketGet/delete-student-role")
    public NotificationResponse deleteStudentRoleNotification(NotificationMessage message) {
        return NotificationResponse.fromMessage(message, "deleteRole");
    }

    /**
     * This method maps @MessageMapping endpoint to the @SendTo endpoint. Called when something is sent to
     * the MessageMapping endpoint. This is triggered when a user has been added student role
     * @param message Information about adding student role
     * @return Returns the message given.
     */
    @MessageMapping("/add-student-role")
    @SendTo("/webSocketGet/add-student-role")
    public NotificationResponse addStudentRoleNotification(NotificationMessage message) {
        return NotificationResponse.fromMessage(message, "addRole");
    }


}
