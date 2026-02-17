package ctts.controller;

import ctts.dto.ProjectRequest;
import ctts.entity.Project;
import ctts.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // 🔥 Admin create project
    @PostMapping
    public Project createProject(@RequestBody ProjectRequest request) {
        return projectService.createProject(request);
    }

    // 🔥 Role based project view
    @GetMapping
    public List<Project> getProjects() {
        return projectService.getProjectsForLoggedUser();
    }

    // 🔥 Admin delete project
    @DeleteMapping("/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "Project deleted successfully";
    }

    // 🔥 Admin update project
    @PutMapping("/{id}")
    public Project updateProject(@PathVariable Long id,
                                 @RequestBody ProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    // 👨‍💻 Developer Dashboard
    @GetMapping("/developer")
    public List<Project> getDeveloperProjects() {
        return projectService.getProjectsForDeveloper();
    }

    // 👤 Client Dashboard
    @GetMapping("/client")
    public List<Project> getClientProjects() {
        return projectService.getProjectsForClient();
    }



}
