package ctts.controller;

import ctts.entity.Notification;
import ctts.entity.User;
import ctts.repository.UserRepository;
import ctts.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/developer")
@RequiredArgsConstructor
public class DeveloperController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // ✅ Test API
    @GetMapping("/test")
    public String developerTest() {
        return "Developer access granted ✅";
    }

    // 🔔 Get all notifications
    @GetMapping("/notifications")
    public List<Notification> getNotifications() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User developer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationService.getUserNotifications(developer);
    }

    // 🔔 Get unread notification count
    @GetMapping("/notifications/unread-count")
    public long getUnreadCount() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User developer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationService.getUnreadCount(developer);
    }

    // 🔔 Mark notification as read
    @PutMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {

        notificationService.markAsRead(id);

        return "Notification marked as read";
    }
}
