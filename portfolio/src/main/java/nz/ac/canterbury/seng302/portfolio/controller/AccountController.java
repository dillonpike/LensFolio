package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import java.util.List;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.NotificationGroup;
import nz.ac.canterbury.seng302.portfolio.model.NotificationHighFive;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utility.DateUtility;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***
 * Controller receives HTTP GET, POST, PUT, DELETE calls for account page
 */
@Controller
public class AccountController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private ProjectService projectService;

    public static final String USER_ID_ATTRIBUTE_NAME = "userId";

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    /***
     * GET method for account controller to generate user's info
     *
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param userIdInput ID for the current login user
     * @return Account page which including user's info
     */
    @GetMapping("/account")
    public String showAccountPage(
            Model model,
            HttpServletRequest request,
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "userId") String userIdInput
    ) {
        UserResponse getUserByIdReply;
        Integer id = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, id);
        elementService.addUpdateMessage(model, request);
        try {
            int userId = Integer.parseInt(userIdInput);
            model.addAttribute("isAuthorised", (id==userId));
            getUserByIdReply = registerClientService.getUserData(userId);
            if (getUserByIdReply.getEmail().length() == 0) {
                model.addAttribute(USER_ID_ATTRIBUTE_NAME, id);
                return "404NotFound";
            }
            elementService.addRoles(model, getUserByIdReply);
            model.addAttribute("viewedUserId", userId);
            model.addAttribute("firstName", getUserByIdReply.getFirstName());
            model.addAttribute("lastName", getUserByIdReply.getLastName());
            model.addAttribute("username", getUserByIdReply.getUsername());
            model.addAttribute("middleName", getUserByIdReply.getMiddleName());
            model.addAttribute("nickName", getUserByIdReply.getNickname());
            model.addAttribute("email", getUserByIdReply.getEmail());
            model.addAttribute("personalPronouns", getUserByIdReply.getPersonalPronouns());
            model.addAttribute("bio", getUserByIdReply.getBio());
            String fullName = getUserByIdReply.getFirstName() + " " + getUserByIdReply.getMiddleName() + " " + getUserByIdReply.getLastName();
            model.addAttribute("fullName", fullName);
            model.addAttribute(USER_ID_ATTRIBUTE_NAME, id);
            model.addAttribute("dateAdded", DateUtility.getDateAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("monthsSinceAdded", DateUtility.getDateSinceAddedString(getUserByIdReply.getCreated()));
            model.addAttribute("userImage", photoService.getPhotoPath(getUserByIdReply.getProfileImagePath(), userId));

            Project project = projectService.getProjectById(0);
            model.addAttribute("project", project);

            Evidence evidence = new Evidence();
            model.addAttribute("evidence", evidence);

            List<Evidence> evidenceList = evidenceService.getEvidences(userId);
            for (Evidence eachEvidence:evidenceList) {
                eachEvidence.setHighFivers(evidenceService.getHighFivers(eachEvidence));
            }
            model.addAttribute("evidences", evidenceList);

        } catch (StatusRuntimeException e) {
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            logger.error("Error while showing account page {}", e.getMessage());
        } catch (NumberFormatException numberFormatException) {
            model.addAttribute(USER_ID_ATTRIBUTE_NAME, id);
            return "404NotFound";
        }

        return "account";
    }


    /***
     * Handler for HTTP POST request, followed by (../backToAccountPage)
     * We load the current userID from HTML file which contains current userID,
     * redirect to account page
     *
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param userId userId ID for the current login user
     * @param rm attributes pass to other controller
     * @return Account page with user id
     */
    @PostMapping("/backToAccountPage")
    public String moveToAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute("userId") int userId,
            RedirectAttributes rm
    ) {
        rm.addAttribute(USER_ID_ATTRIBUTE_NAME,userId);
        return "redirect:account";
    }

    /***
     * Used to handle the interaction between a piece of evidence being highfived
     * and the notification being shown through the header.
     *
     * @return Send a notification to the header to display a highfive notification.
     */
    @MessageMapping("/high-fived-evidence")
    @SendTo("/webSocketGet/notification-of-highfive")
    public NotificationHighFive refreshGroupSettingsOutside(NotificationHighFive notificationHighFive) {
        return notificationHighFive;
    }

}
