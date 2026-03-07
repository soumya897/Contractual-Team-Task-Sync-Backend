package ctts.service;

import ctts.entity.ProjectStatus;
import ctts.repository.ProjectRepository;
import ctts.dto.ProfileResponse;
import ctts.entity.Role;
import ctts.entity.User;
import ctts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileResponse getProfile() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        long totalProjects = 0;
        long totalDevelopers = 0;
        long totalClients = 0;
        long completedProjects = 0;
        long ongoingProjects = 0;

        if (user.getRole() == Role.PROJECT_MANAGER) {
            totalProjects = projectRepository.countByCreatedBy(user);
            completedProjects = projectRepository.countByStatusAndCreatedBy(ProjectStatus.COMPLETED, user);
            ongoingProjects = projectRepository.countByStatusAndCreatedBy(ProjectStatus.ONGOING, user);
            totalDevelopers = userRepository.countByRoleAndCreatedByProjectManager(Role.DEVELOPER, user);
            totalClients = userRepository.countByRoleAndCreatedByProjectManager(Role.CLIENT, user);
        }

        return ProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .ph(user.getPh())
                .role(user.getRole().name())
                .totalProjects(totalProjects)
                .totalDevelopers(totalDevelopers)
                .totalClients(totalClients)
                .completedProjects(completedProjects)
                .ongoingProjects(ongoingProjects)
                .build();
    }

    public ProfileResponse updateProfile(String name, String ph) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        user.setName(name);
        user.setPh(ph);

        userRepository.save(user);

        return getProfile();
    }

    public String changePassword(String oldPassword, String newPassword) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password changed successfully!";
    }
}