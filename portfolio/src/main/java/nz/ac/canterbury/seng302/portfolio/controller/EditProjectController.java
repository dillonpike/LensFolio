package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.ElementService;
import nz.ac.canterbury.seng302.portfolio.service.PermissionService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private ElementService elementService;

    /***
     * Handler methods for mapping HTTP POST request, followed by the URL(../edit-project)
     *
     * @param projectName Current project name
     * @param projectStartDate Current project start date
     * @param projectEndDate Current project end date
     * @param projectDescription Current project description
     * @return Redirect to detail page(send HTTP GET request)
     * @throws Exception If invalid input has been caught
     */
    @PostMapping("/edit-project")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="name") String projectName,
            @RequestParam(value="startDateString") String projectStartDate,
            @RequestParam(value="endDateString") String projectEndDate,
            @RequestParam(value="description") String projectDescription,
            Model model
    ) throws Exception {
        Integer userID = userAccountClientService.getUserIDFromAuthState(principal);
        elementService.addHeaderAttributes(model, userID);

        if (permissionService.isValidToModifyProjectPage(userID)) {
            // Gets the project with id 0 to plonk on the page
            Project newProject = projectService.getProjectById(0);
            newProject.setName(projectName);
            newProject.setStartDateString(projectStartDate);
            newProject.setEndDateString(projectEndDate);
            newProject.setDescription(projectDescription);
            projectService.updateProject(newProject);
        }
        return "redirect:/details";
    }
}
