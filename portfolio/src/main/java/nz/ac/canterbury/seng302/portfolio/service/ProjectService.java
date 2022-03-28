package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository repository;

    /**
     * Get list of all projects
     */
    public List<Project> getAllProjects() {
        List<Project> list = (List<Project>) repository.findAll();
        return list;
    }

    /**
     * Get project by id
     * @param id The project Id
     * @return The project from the Database
     * @throws Exception Throws if the project is not found.
     */
    public Project getProjectById(Integer id) throws Exception {

        Optional<Project> project = repository.findById(id);
        if(project!=null) {
            return project.get();
        } else {
            throw new Exception("Project not found");
        }
    }

    /**
     * Get project by id
     * @param id Id of project
     * @return Project from the Database
     * @throws Exception Throws if project is not found.
     */
    public Project UpdateProjectById(Integer id) throws Exception {

        Optional<Project> project = repository.findById(id);
        if(project!=null) {
            return project.get();
        } else {
            throw new Exception("Project not found");
        }
    }

    /**
     *
     * updates a project.
     */
    public Project updateProject(Project project) {
        Optional<Project> pOptional = repository.findById((Integer) project.getId());

        if(pOptional != null) {
            Project projectUpdate = pOptional.get();
            projectUpdate.setDescription(project.getDescription());
            projectUpdate.setStartDate(project.getStartDate());
            projectUpdate.setEndDate(project.getEndDate());
            projectUpdate.setName(project.getName());

            projectUpdate = repository.save(projectUpdate);
            return projectUpdate;
        }
        else {
            project = repository.save(project);
            return project;
        }
    }

}
