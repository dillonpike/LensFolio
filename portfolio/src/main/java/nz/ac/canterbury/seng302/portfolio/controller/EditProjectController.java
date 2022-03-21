package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.DateValidationService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private DateValidationService dateValidationService;

    @GetMapping("/edit-project")
    public String projectForm(Model model) throws Exception {
        // Gets the project with id 0 to plonk on the page
        Project project = projectService.getProjectById(0);
        /* Add project details to the model */
        model.addAttribute("projectName", project.getName());
        model.addAttribute("projectStartDate", project.getStartDateString());
        model.addAttribute("projectEndDate", project.getEndDateString());
        model.addAttribute("projectDescription", project.getDescription());
        model.addAttribute("projectStartDateError", "");
        model.addAttribute("projectDateError", "");

        /* Return the name of the Thymeleaf template */
        return "editProject";
    }

    @PostMapping("/edit-project")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") String projectStartDate,
            @RequestParam(value="projectEndDate") String projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription,
            Model model
    ) throws Exception {
        // Gets the project with id 0 to plonk on the page
        Project newProject = projectService.getProjectById(0);
        newProject.setName(projectName);
        newProject.setStartDateString(projectStartDate);
        newProject.setEndDateString(projectEndDate);
        newProject.setDescription(projectDescription);
        projectService.updateProject(newProject);
        return "redirect:/edit-project";
    }

    @RequestMapping(value="/edit-project/error", method= RequestMethod.POST)
    public String updateProjectRangeErrors(@RequestParam(value="projectStartDate") String projectStartDate,
        @RequestParam(value="projectEndDate") String projectEndDate,
        Model model) {
        model.addAttribute("projectDateError",
                dateValidationService.validateProjectStartDate(projectStartDate, projectEndDate));
        return "editProject :: #projectDateError";
    }

}
