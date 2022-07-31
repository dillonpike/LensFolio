package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Objects;

/**
 * Service layer for permission checks(User's operations), contains methods to the user's permission
 */
@Service
public class PermissionService {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;


    /**
     * Function to validate user's current operation(Delete/add role).
     * This function is to help get user's permission update immediately, make sure user cannot overstep.
     *
     * @param targetRole The target role object user want to modify
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return True if current user has the permission
     */
    public boolean isValidToDeleteRole(String targetRole,
                            @AuthenticationPrincipal AuthState principal,
                            Model model
    ) {
        UserResponse getUserByIdReply;
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        getUserByIdReply = registerClientService.getUserData(id);

        //Get the current user's highest role
        String highestRole = elementService.getUserHighestRole(getUserByIdReply);
        // Decline users' request if they are current a student
        if (highestRole.equals("student")) {
            return false;
        }
        // Decline users' request to course admin if they are current a teacher
        if (highestRole.equals("teacher")) {
            return !targetRole.equals("admin") && !Objects.equals(targetRole, "COURSE_ADMINISTRATOR");
        }
        return true;
    }

    /**
     * Function to validate user's current operation(edit/add/delete)
     * This function can be used in different requests.
     *
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return false if user's role has been changed to student
     */
    public boolean isValid(@AuthenticationPrincipal AuthState principal,
                           Model model) {
        UserResponse getUserByIdReply;
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        getUserByIdReply = registerClientService.getUserData(id);

        //Get the current user's highest role
        String highestRole = elementService.getUserHighestRole(getUserByIdReply);
        // Decline users' request if they are current a student
        return !highestRole.equals("student");
    }
}
