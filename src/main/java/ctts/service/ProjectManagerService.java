package ctts.service;
import ctts.entity.ProjectStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import ctts.dto.ProjectManagerDashboardResponse;
import ctts.entity.*;
import ctts.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectManagerService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User getLoggedInProjectManager() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Project Manager not found"));
    }

    public List<Project> getAllProjects(String search) {
        User pm = getLoggedInProjectManager();
        if (search == null || search.trim().isEmpty()) {
            return projectRepository.findByCreatedBy(pm);
        }
        return projectRepository.searchProjectsByProjectManager(pm, search);
    }

    public List<User> getAllClients(String search) {
        User pm = getLoggedInProjectManager();
        if (search == null || search.trim().isEmpty()) {
            return userRepository.findByRoleAndCreatedByProjectManager(Role.CLIENT, pm);
        }
        return userRepository.searchUsersByRoleAndProjectManager(Role.CLIENT, pm, search);
    }

    public List<User> getAllDevelopers(String search) {
        User pm = getLoggedInProjectManager();
        if (search == null || search.trim().isEmpty()) {
            return userRepository.findByRoleAndCreatedByProjectManager(Role.DEVELOPER, pm);
        }
        return userRepository.searchUsersByRoleAndProjectManager(Role.DEVELOPER, pm, search);
    }

    public ProjectManagerDashboardResponse getDashboardStats() {
        User pm = getLoggedInProjectManager();

        long totalProjects = projectRepository.countByCreatedBy(pm);
        long completedProjects = projectRepository.countByStatusAndCreatedBy(ProjectStatus.COMPLETED, pm);
        long totalClients = userRepository.countByRoleAndCreatedByProjectManager(Role.CLIENT, pm);
        long totalDevelopers = userRepository.countByRoleAndCreatedByProjectManager(Role.DEVELOPER, pm);

        return new ProjectManagerDashboardResponse(
                totalProjects, completedProjects, totalClients, totalDevelopers
        );
    }

    public User createClient(User request) {
        request.setRole(Role.CLIENT);
        request.setCreatedByProjectManager(getLoggedInProjectManager());

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(request);
    }

    public User updateClient(Long id, User updated) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        user.setName(updated.getName());
        user.setPh(updated.getPh());
        user.setEmail(updated.getEmail());

        return userRepository.save(user);
    }

    public void deleteClient(Long id) {
        userRepository.deleteById(id);
    }

    public User createDeveloper(User request) {
        request.setRole(Role.DEVELOPER);
        request.setCreatedByProjectManager(getLoggedInProjectManager());

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(request);
    }

    public User updateDeveloper(Long id, User updated) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Developer not found"));

        user.setName(updated.getName());
        user.setPh(updated.getPh());
        user.setEmail(updated.getEmail());

        return userRepository.save(user);
    }

    public void deleteDeveloper(Long id) {
        userRepository.deleteById(id);
    }
}