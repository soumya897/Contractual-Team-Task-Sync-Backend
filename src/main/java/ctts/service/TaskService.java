package ctts.service;

import ctts.dto.TaskRequest;
import ctts.entity.*;
import ctts.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;


    // ✅ GET ALL TASKS (Admin only)
    public List<Task> getTasksByProject(Long projectId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can view all tasks");
        }

        return taskRepository.findByProjectId(projectId);
    }


    // ✅ CREATE TASK (Admin + Developer)
    public Task createTask(TaskRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❌ CLIENT cannot create task
        if (currentUser.getRole() == Role.CLIENT) {
            throw new RuntimeException("Client cannot create task");
        }

        // 🔎 Validate Project
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // 🔎 Validate Developer
        User developer = userRepository.findById(request.getDeveloperId())
                .orElseThrow(() -> new RuntimeException("Developer not found"));

        // ❌ Ensure assigned user is actually a developer
        if (developer.getRole() != Role.DEVELOPER) {
            throw new RuntimeException("Assigned user must be a developer");
        }

        // ❌ Ensure developer belongs to this project
        if (!project.getDevelopers().contains(developer)) {
            throw new RuntimeException("Developer is not assigned to this project");
        }

        // ❌ Validate required fields
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("Task title is required");
        }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new RuntimeException("Task description is required");
        }

        // ✅ Build task
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(false)
                .project(project)
                .developer(developer)
                .build();

        return taskRepository.save(task);
    }

    // ✅ DELETE TASK
    public void deleteTask(Long taskId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // ✅ ADMIN can delete any task
        if (currentUser.getRole() == Role.ADMIN) {
            taskRepository.delete(task);
            return;
        }

        // ✅ Developer can delete only own task
        if (currentUser.getRole() == Role.DEVELOPER &&
                task.getDeveloper().getId().equals(currentUser.getId())) {

            taskRepository.delete(task);
            return;
        }

        throw new RuntimeException("Not allowed to delete this task");
    }

    // ✅ MARK COMPLETE (Developer Only)
    public Task markComplete(Long taskId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // ❌ Only developer can mark complete
        if (currentUser.getRole() != Role.DEVELOPER) {
            throw new RuntimeException("Only developer can mark complete");
        }

        // ❌ Developer can only mark their own task
        if (!task.getDeveloper().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own tasks");
        }

        task.setCompleted(true);

        return taskRepository.save(task);
    }
    // ✅ PROJECT COMPLETION %
    public double getProjectCompletion(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        long totalTasks = taskRepository.countByProject(project);

        if (totalTasks == 0) return 0;

        long completedTasks =
                taskRepository.countByProjectAndCompletedTrue(project);

        return (completedTasks * 100.0) / totalTasks;
    }


    // ✅ UPDATE TASK
    public Task updateTask(Long taskId, TaskRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // ❌ Client cannot update
        if (currentUser.getRole() == Role.CLIENT) {
            throw new RuntimeException("Client cannot update task");
        }

        // Developer can update only own task
        if (currentUser.getRole() == Role.DEVELOPER &&
                !task.getDeveloper().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can update only your own tasks");
        }

        // Update fields if present
        if (request.getTitle() != null)
            task.setTitle(request.getTitle());

        if (request.getDescription() != null)
            task.setDescription(request.getDescription());

        if (request.getCompleted() != null)
            task.setCompleted(request.getCompleted());

        return taskRepository.save(task);
    }

}
