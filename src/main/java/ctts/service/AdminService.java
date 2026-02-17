package ctts.service;

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
}
