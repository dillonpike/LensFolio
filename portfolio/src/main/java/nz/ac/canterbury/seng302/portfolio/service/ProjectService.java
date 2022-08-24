package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

/***
 * Contains methods for saving, deleting, updating and retrieving project objects to the database.
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);


    /**
     * Get list of all projects.
     * @return List of projects saved in the database
     */
    public List<Project> getAllProjects() {
        return (List<Project>) repository.findAll();
    }

    /**
     * Get project by id
     * @param id The project Id
     * @return The project from the Database
     * @throws ObjectNotFoundException Throws if the project is not found.
     */
    public Project getProjectById(Integer id) throws ObjectNotFoundException {

        Optional<Project> project = repository.findById(id);
        if (project.isPresent()) {
            return project.get();
        } else {
            throw new ObjectNotFoundException(id, "Unknown project");
        }
    }

    /**
     * Get project by id
     * @param id Id of project
     * @return Project from the Database
     * @throws ObjectNotFoundException Throws if project is not found.
     */
    public Project updateProjectById(Integer id) throws ObjectNotFoundException {

        Optional<Project> project = repository.findById(id);
        if (project.isPresent()) {
            return project.get();
        } else {
            throw new ObjectNotFoundException(id, "Project not found");
        }
    }

    /**
     * Updates a project in the Database and returns edited project.
     * @param project Project with new data
     * @return New Project that is saved in the database
     */
    public Project updateProject(Project project) {
        Optional<Project> pOptional = repository.findById((Integer) project.getId());

        if (pOptional.isPresent()) {
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

    /**
     * Saves a project in the Database and returns the saved project.
     * @param project New project
     * @return New Project that is saved in the database
     */
    public Project saveProject(Project project) {
        Project newProject;
        try {
            newProject = repository.save(project);
            return newProject;
        } catch (Exception e) {
            logger.error("Failed to save new project");
            return project;
        }
    }

}
