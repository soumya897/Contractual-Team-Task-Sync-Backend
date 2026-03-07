package ctts.controller;
import ctts.dto.TaskRequest;
import ctts.entity.Task;
import ctts.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // ✅ Create Task (Project Manager only)
    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    // ✅ Delete Task (Project Manager only)
    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "Task deleted successfully";
    }

    @PutMapping("/{id}/complete")
    public Task markComplete(@PathVariable Long id) {
        return taskService.markComplete(id);
    }

    @GetMapping("/project/{projectId}/completion")
    public double getCompletion(@PathVariable Long projectId) {
        return taskService.getProjectCompletion(projectId);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        return taskService.updateTask(id, request);
    }

    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable Long projectId) {
        return taskService.getTasksByProject(projectId);
    }
}