package ctts.service;

import ctts.entity.ProjectStatus;
import ctts.repository.ProjectRepository;
import ctts.dto.ProfileResponse;
import ctts.entity.Role;
import ctts.entity.User;
import ctts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

    public class ProfileService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

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

            if (user.getRole() == Role.ADMIN) {

                totalProjects = projectRepository.count();

                completedProjects =
                        projectRepository.countByStatus(ProjectStatus.COMPLETED);

                ongoingProjects =
                        projectRepository.countByStatus(ProjectStatus.ONGOING);

                totalDevelopers = userRepository.countByRole(Role.DEVELOPER);

                totalClients = userRepository.countByRole(Role.CLIENT);
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

        // 🔥 ADD THIS METHOD HERE
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
    }

