package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Autowired
    private GroupService groupService;

    private List<UserResponse> userResponseList;

    /**
     * Function to validate user's current operation(Delete/add role).
     * This function is to help get user's permission update immediately, make sure user cannot overstep.
     *
     * @param targetRole The target role object user want to modify
     * @return True if current user has the permission
     */
    public boolean isValidToModifyRole(String targetRole,
                                       Integer userId
    ) {
        UserResponse getUserByIdReply;
        getUserByIdReply = registerClientService.getUserData(userId);

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
     * @return false if user's role has been changed to student
     */
    public boolean isValidToModifyProjectPage(Integer userID) {
        UserResponse getUserByIdReply = registerClientService.getUserData(userID);

        //Get the current user's highest role
        String highestRole = elementService.getUserHighestRole(getUserByIdReply);
        // Decline users' request if they are current a student
        return !highestRole.equals("student");
    }

    /**
     * Method to validate if user have write-permission on particular group setting page
     * @param groupId current group id
     * @param userId current user id
     * @return true if user is in the group, false if not.
     */
    public boolean isValidToModifyGroupSettingPage(int groupId, int userId) {
        GroupDetailsResponse groupDetailsResponse = groupService.getGroupDetails(groupId);
        userResponseList = groupDetailsResponse.getMembersList();
        for (UserResponse userResponse : userResponseList) {
            if (userResponse.getId() == userId) {
                return true;
            }
        }
        return false;
    }

}
