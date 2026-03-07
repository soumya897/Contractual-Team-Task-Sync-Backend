package ctts.service;
import ctts.entity.ProjectStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import ctts.dto.AdminDashboardResponse;
import ctts.entity.*;
import ctts.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    private User getLoggedInAdmin() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public List<Project> getAllProjects(String search) {
        User admin = getLoggedInAdmin();
        if (search == null || search.trim().isEmpty()) {
            return projectRepository.findByCreatedBy(admin); // 🔥 Filtered by admin
        }
        return projectRepository.searchProjectsByAdmin(admin, search); // 🔥 Filtered by admin
    }

    public List<User> getAllClients(String search) {
        User admin = getLoggedInAdmin();
        if (search == null || search.trim().isEmpty()) {
            return userRepository.findByRoleAndCreatedByAdmin(Role.CLIENT, admin); // 🔥 Filtered by admin
        }
        return userRepository.searchUsersByRoleAndAdmin(Role.CLIENT, admin, search); // 🔥 Filtered by admin
    }

    public List<User> getAllDevelopers(String search) {
        User admin = getLoggedInAdmin();
        if (search == null || search.trim().isEmpty()) {
            return userRepository.findByRoleAndCreatedByAdmin(Role.DEVELOPER, admin); // 🔥 Filtered by admin
        }
        return userRepository.searchUsersByRoleAndAdmin(Role.DEVELOPER, admin, search); // 🔥 Filtered by admin
    }

    public AdminDashboardResponse getDashboardStats() {
        User admin = getLoggedInAdmin();

        // 🔥 Now counts ONLY stats for this specific admin
        long totalProjects = projectRepository.countByCreatedBy(admin);
        long completedProjects = projectRepository.countByStatusAndCreatedBy(ProjectStatus.COMPLETED, admin);
        long totalClients = userRepository.countByRoleAndCreatedByAdmin(Role.CLIENT, admin);
        long totalDevelopers = userRepository.countByRoleAndCreatedByAdmin(Role.DEVELOPER, admin);

        return new AdminDashboardResponse(
                totalProjects, completedProjects, totalClients, totalDevelopers
        );
    }

    public User createClient(User request) {
        request.setRole(Role.CLIENT);
        request.setCreatedByAdmin(getLoggedInAdmin()); // 🔥 Link client to this admin

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
        request.setCreatedByAdmin(getLoggedInAdmin()); // 🔥 Link developer to this admin

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
