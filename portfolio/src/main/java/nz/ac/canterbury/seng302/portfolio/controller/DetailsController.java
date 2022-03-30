package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import java.util.List;

/**
 * Controller for the display project details page
 */
@Controller
public class DetailsController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountService userAccountService;

    /***
     * GET request method, followed by the request URL(../details)
     *
     * @param principal
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return TeacherProjectDetails or userProjectDetails which is dependent on user's role
     * @throws Exception
     */
    @GetMapping("/details")
    public String details(@AuthenticationPrincipal AuthState principal, Model model) throws Exception {
        /* Add project details to the model */
        // Gets the project with id 0 to plonk on the page
        Project project = projectService.getProjectById(0);
        model.addAttribute("project", project);
        
        List<Sprint> sprintList = sprintService.getAllSprintsOrdered();
        model.addAttribute("sprints", sprintList);

        UserResponse getUserByIdReplyHeader;
        Integer id = userAccountService.getUserIDFromAuthState(principal);
        getUserByIdReplyHeader = registerClientService.getUserData(id);
        String fullNameHeader = getUserByIdReplyHeader.getFirstName() + " " + getUserByIdReplyHeader.getMiddleName() + " " + getUserByIdReplyHeader.getLastName();
        model.addAttribute("fullName", fullNameHeader);
        model.addAttribute("userId", id);

        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        /* Return the name of the Thymeleaf template */
        // detects the role of the current user and returns appropriate page
        if (!role.equals("teacher")) {
            return "teacherProjectDetails";
        } else {
            return "userProjectDetails";
        }
    }

}
