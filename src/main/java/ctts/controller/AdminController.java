package ctts.controller;

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
}
