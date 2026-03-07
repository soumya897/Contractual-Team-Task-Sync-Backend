package ctts.service;

import ctts.dto.ProjectRequest;
import ctts.entity.Project;
import ctts.entity.ProjectStatus;
import ctts.entity.Role;
import ctts.entity.User;
import ctts.repository.ProjectRepository;
import ctts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 🔥 PROJECT MANAGER → Create Project
    public Project createProject(ProjectRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User pm = userRepository.findByEmail(email).orElseThrow();

        if (pm.getRole() != Role.PROJECT_MANAGER) {
            throw new RuntimeException("Only project manager can create project");
        }

        User client = userRepository.findById(request.getClientId()).orElseThrow();
        List<User> developers = userRepository.findAllById(request.getDeveloperIds());

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(ProjectStatus.ONGOING)
                .client(client)
                .developers(developers)
                .createdBy(pm)
                .build();

        Project savedProject = projectRepository.save(project);

        for (User dev : developers) {
            notificationService.createNotification(
                    dev,
                    "Project Manager " + pm.getName() + " assigned you to a new project: '" + savedProject.getTitle() + "'"
            );
        }

        return savedProject;
    }

    public Project updateProject(Long projectId, ProjectRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRole() != Role.PROJECT_MANAGER) {
            throw new RuntimeException("Only project manager can update project");
        }

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        Set<User> oldDevelopers = new HashSet<>(project.getDevelopers());

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus());

        User client = userRepository.findById(request.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));
        project.setClient(client);

        List<User> newDevelopers = userRepository.findAllById(request.getDeveloperIds());
        project.setDevelopers(newDevelopers);

        Project updatedProject = projectRepository.save(project);

        for (User dev : newDevelopers) {
            if (!oldDevelopers.contains(dev)) {
                notificationService.createNotification(
                        dev,
                        "Project Manager " + currentUser.getName() + " added you to the project: '" + updatedProject.getTitle() + "'"
                );
            }
        }

        return updatedProject;
    }

    public void deleteProject(Long projectId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRole() != Role.PROJECT_MANAGER) {
            throw new RuntimeException("Only project manager can delete project");
        }

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }

    public List<Project> getProjectsForLoggedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getRole() == Role.PROJECT_MANAGER) {
            return projectRepository.findByCreatedBy(user);
        }
        if (user.getRole() == Role.CLIENT) {
            return projectRepository.findByClient(user);
        }
        if (user.getRole() == Role.DEVELOPER) {
            return projectRepository.findByDevelopersContaining(user);
        }
        return List.of();
    }

    public List<Project> getProjectsForDeveloper() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User developer = userRepository.findByEmail(email).orElseThrow();

        if (developer.getRole() != Role.DEVELOPER) {
            throw new RuntimeException("Only developer can access this");
        }

        return projectRepository.findByDevelopersContaining(developer);
    }

    public List<Project> getProjectsForClient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User client = userRepository.findByEmail(email).orElseThrow();

        if (client.getRole() != Role.CLIENT) {
            throw new RuntimeException("Only client can access this");
        }

        return projectRepository.findByClient(client);
    }
}