package ctts.service;

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

    // ✅ Get all projects
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // ✅ Get all clients
    public List<User> getAllClients() {
        return userRepository.findByRole(Role.CLIENT);
    }

    // ✅ Get all developers
    public List<User> getAllDevelopers() {
        return userRepository.findByRole(Role.DEVELOPER);
    }

    public AdminDashboardResponse getDashboardStats() {

        long totalProjects = projectRepository.count();
        long completedProjects = projectRepository.countByStatus("COMPLETED");

        long totalClients = userRepository.countByRole(Role.CLIENT);
        long totalDevelopers = userRepository.countByRole(Role.DEVELOPER);

        return new AdminDashboardResponse(
                totalProjects,
                completedProjects,
                totalClients,
                totalDevelopers
        );
    }

    public User createClient(User request) {
        request.setRole(Role.CLIENT);
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
