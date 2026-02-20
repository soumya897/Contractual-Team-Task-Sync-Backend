package ctts.controller;

import ctts.entity.Notification;
import ctts.dto.AdminDashboardResponse;
import ctts.entity.Project;
import ctts.entity.User;
import ctts.repository.UserRepository;
import ctts.service.AdminService;
import ctts.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")   // ✅ Important: match SecurityConfig
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // ✅ Test API
    @GetMapping("/test")
    public String adminTest() {
        return "Admin access granted ✅";
    }

    // 🔥 Get all projects (WITH SEARCH)
    @GetMapping("/projects")
    public List<Project> getAllProjects(@RequestParam(required = false) String search) {
        return adminService.getAllProjects(search);
    }

    // 🔥 Get all clients (WITH SEARCH)
    @GetMapping("/clients")
    public List<User> getAllClients(@RequestParam(required = false) String search) {
        return adminService.getAllClients(search);
    }

    // 🔥 Get all developers (WITH SEARCH)
    @GetMapping("/developers")
    public List<User> getAllDevelopers(@RequestParam(required = false) String search) {
        return adminService.getAllDevelopers(search);
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminService.getDashboardStats();
    }
    @PostMapping("/clients")
    public User createClient(@RequestBody User user) {
        return adminService.createClient(user);
    }

    @PutMapping("/clients/{id}")
    public User updateClient(@PathVariable Long id,
                             @RequestBody User user) {
        return adminService.updateClient(id, user);
    }

    @DeleteMapping("/clients/{id}")
    public String deleteClient(@PathVariable Long id) {
        adminService.deleteClient(id);
        return "Client deleted successfully";
    }

    @PostMapping("/developers")
    public User createDeveloper(@RequestBody User user) {
        return adminService.createDeveloper(user);
    }

    @PutMapping("/developers/{id}")
    public User updateDeveloper(@PathVariable Long id,
                                @RequestBody User user) {
        return adminService.updateDeveloper(id, user);
    }

    @DeleteMapping("/developers/{id}")
    public String deleteDeveloper(@PathVariable Long id) {
        adminService.deleteDeveloper(id);
        return "Developer deleted successfully";
    }

    // 🔔 Get all notifications for Admin
    @GetMapping("/notifications")
    public List<Notification> getNotifications() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return notificationService.getUserNotifications(admin);
    }

    // 🔔 Get unread notification count for Admin
    @GetMapping("/notifications/unread-count")
    public long getUnreadCount() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return notificationService.getUnreadCount(admin);
    }

    // 🔔 Mark notification as read
    @PutMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "Notification marked as read";
    }



}
