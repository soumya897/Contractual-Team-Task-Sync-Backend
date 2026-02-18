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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // 🔥 ADMIN → Create Project
    public Project createProject(ProjectRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByEmail(email).orElseThrow();

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can create project");
        }

        User client = userRepository.findById(request.getClientId())
                .orElseThrow();

        List<User> developers = userRepository.findAllById(request.getDeveloperIds());

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(ProjectStatus.ONGOING)
                .client(client)
                .developers(developers)
                .createdBy(admin)
                .build();

        return projectRepository.save(project);
    }

    // 🔥 ROLE BASED PROJECT VIEW
    public List<Project> getProjectsForLoggedUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getRole() == Role.ADMIN) {
            return projectRepository.findAll();
        }

        if (user.getRole() == Role.CLIENT) {
            return projectRepository.findByClient(user);
        }

        if (user.getRole() == Role.DEVELOPER) {
            return projectRepository.findByDevelopersContaining(user);
        }

        return List.of();
    }

    public Project updateProject(Long projectId, ProjectRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only ADMIN can update
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can update project");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Update basic fields
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus());


        // Update client
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        project.setClient(client);

        // Update developers
        List<User> developers = userRepository.findAllById(request.getDeveloperIds());
        project.setDevelopers(developers);

        return projectRepository.save(project);
    }

    public void deleteProject(Long projectId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only ADMIN can delete project
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can delete project");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.delete(project);
    }
    public List<Project> getProjectsForDeveloper() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User developer = userRepository.findByEmail(email)
                .orElseThrow();

        if (developer.getRole() != Role.DEVELOPER) {
            throw new RuntimeException("Only developer can access this");
        }

        return projectRepository.findByDevelopersContaining(developer);
    }


    public List<Project> getProjectsForClient() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User client = userRepository.findByEmail(email)
                .orElseThrow();

        if (client.getRole() != Role.CLIENT) {
            throw new RuntimeException("Only client can access this");
        }

        return projectRepository.findByClient(client);
    }


}
