package ctts.controller;

import ctts.entity.Notification;
import ctts.dto.ProjectManagerDashboardResponse;
import ctts.entity.Project;
import ctts.entity.User;
import ctts.repository.UserRepository;
import ctts.service.ProjectManagerService;
import ctts.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-manager")
@RequiredArgsConstructor
public class ProjectManagerController {

    private final ProjectManagerService projectManagerService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping("/test")
    public String pmTest() {
        return "Project Manager access granted ✅";
    }

    @GetMapping("/projects")
    public List<Project> getAllProjects(@RequestParam(required = false) String search) {
        return projectManagerService.getAllProjects(search);
    }

    @GetMapping("/clients")
    public List<User> getAllClients(@RequestParam(required = false) String search) {
        return projectManagerService.getAllClients(search);
    }

    @GetMapping("/developers")
    public List<User> getAllDevelopers(@RequestParam(required = false) String search) {
        return projectManagerService.getAllDevelopers(search);
    }

    @GetMapping("/dashboard")
    public ProjectManagerDashboardResponse getDashboard() {
        return projectManagerService.getDashboardStats();
    }

    @PostMapping("/clients")
    public User createClient(@RequestBody User user) {
        return projectManagerService.createClient(user);
    }

    @PutMapping("/clients/{id}")
    public User updateClient(@PathVariable Long id, @RequestBody User user) {
        return projectManagerService.updateClient(id, user);
    }

    @DeleteMapping("/clients/{id}")
    public String deleteClient(@PathVariable Long id) {
        projectManagerService.deleteClient(id);
        return "Client deleted successfully";
    }

    @PostMapping("/developers")
    public User createDeveloper(@RequestBody User user) {
        return projectManagerService.createDeveloper(user);
    }

    @PutMapping("/developers/{id}")
    public User updateDeveloper(@PathVariable Long id, @RequestBody User user) {
        return projectManagerService.updateDeveloper(id, user);
    }

    @DeleteMapping("/developers/{id}")
    public String deleteDeveloper(@PathVariable Long id) {
        projectManagerService.deleteDeveloper(id);
        return "Developer deleted successfully";
    }

    @GetMapping("/notifications")
    public List<Notification> getNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User pm = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Project Manager not found"));
        return notificationService.getUserNotifications(pm);
    }

    @GetMapping("/notifications/unread-count")
    public long getUnreadCount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User pm = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Project Manager not found"));
        return notificationService.getUnreadCount(pm);
    }

    @PutMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "Notification marked as read";
    }
}