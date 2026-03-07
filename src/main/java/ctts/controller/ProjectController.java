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

    // 🔥 Project Manager create project
    @PostMapping
    public Project createProject(@RequestBody ProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping
    public List<Project> getProjects() {
        return projectService.getProjectsForLoggedUser();
    }

    // 🔥 Project Manager delete project
    @DeleteMapping("/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "Project deleted successfully";
    }

    // 🔥 Project Manager update project
    @PutMapping("/{id}")
    public Project updateProject(@PathVariable Long id, @RequestBody ProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    @GetMapping("/developer")
    public List<Project> getDeveloperProjects() {
        return projectService.getProjectsForDeveloper();
    }

    @GetMapping("/client")
    public List<Project> getClientProjects() {
        return projectService.getProjectsForClient();
    }
}