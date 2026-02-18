package ctts.controller;


import ctts.dto.AdminDashboardResponse;
import ctts.entity.Project;
import ctts.entity.User;
import ctts.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")   // ✅ Important: match SecurityConfig
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ✅ Test API
    @GetMapping("/test")
    public String adminTest() {
        return "Admin access granted ✅";
    }

    // 🔥 Get all projects
    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        return adminService.getAllProjects();
    }

    // 🔥 Get all clients
    @GetMapping("/clients")
    public List<User> getAllClients() {
        return adminService.getAllClients();
    }

    // 🔥 Get all developers
    @GetMapping("/developers")
    public List<User> getAllDevelopers() {
        return adminService.getAllDevelopers();
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



}
