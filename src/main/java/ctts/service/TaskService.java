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
    private final NotificationService notificationService;


    // ✅ GET ALL TASKS (Admin only)
    public List<Task> getTasksByProject(Long projectId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // ADMIN → can view everything
        if (currentUser.getRole() == Role.ADMIN) {
            return taskRepository.findByProject(project);
        }

        // CLIENT → can view only their own project
        if (currentUser.getRole() == Role.CLIENT &&
                project.getClient().getId().equals(currentUser.getId())) {

            return taskRepository.findByProject(project);
        }

        // DEVELOPER → only if assigned to project
        if (currentUser.getRole() == Role.DEVELOPER &&
                project.getDevelopers().contains(currentUser)) {

            return taskRepository.findByProject(project);
        }

        throw new RuntimeException("Not authorized to view tasks of this project");
    }



    // ✅ CREATE TASK (Admin + Developer)
    public Task createTask(TaskRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRole() == Role.CLIENT) {
            throw new RuntimeException("Client cannot create task");
        }

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User developer = userRepository.findById(request.getDeveloperId())
                .orElseThrow(() -> new RuntimeException("Developer not found"));

        if (developer.getRole() != Role.DEVELOPER) {
            throw new RuntimeException("Assigned user must be a developer");
        }

        if (!project.getDevelopers().contains(developer)) {
            throw new RuntimeException("Developer is not assigned to this project");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("Task title is required");
        }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new RuntimeException("Task description is required");
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(false)
                .project(project)
                .developer(developer)
                .build();

        Task savedTask = taskRepository.save(task);

        // 🔔 Notify developer
        notificationService.createNotification(
                developer,
                "You have been assigned a new task: " + savedTask.getTitle()
        );

        return savedTask;
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

        if (currentUser.getRole() != Role.DEVELOPER) {
            throw new RuntimeException("Only developer can mark complete");
        }

        if (!task.getDeveloper().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own tasks");
        }

        task.setCompleted(true);

        Task updatedTask = taskRepository.save(task);

        // 🔔 Notify Admin who created project
        User admin = task.getProject().getCreatedBy();

        notificationService.createNotification(
                admin,
                "Task completed: " + task.getTitle() +
                        " by " + currentUser.getName()
        );

        return updatedTask;
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

        if (currentUser.getRole() == Role.CLIENT) {
            throw new RuntimeException("Client cannot update task");
        }

        if (currentUser.getRole() == Role.DEVELOPER &&
                !task.getDeveloper().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can update only your own tasks");
        }

        // Store old developer
        User oldDeveloper = task.getDeveloper();

        if (request.getTitle() != null)
            task.setTitle(request.getTitle());

        if (request.getDescription() != null)
            task.setDescription(request.getDescription());

        if (request.getCompleted() != null)
            task.setCompleted(request.getCompleted());

        // If developer changed → notify new developer
        if (request.getDeveloperId() != null) {

            User newDeveloper = userRepository.findById(request.getDeveloperId())
                    .orElseThrow(() -> new RuntimeException("Developer not found"));

            task.setDeveloper(newDeveloper);

            if (!newDeveloper.getId().equals(oldDeveloper.getId())) {
                notificationService.createNotification(
                        newDeveloper,
                        "You have been assigned a new task: " + task.getTitle()
                );
            }
        }

        return taskRepository.save(task);
    }

}
