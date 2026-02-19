package ctts.repository;

import ctts.entity.Task;
import ctts.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    long countByProject(Project project);

    long countByProjectAndCompletedTrue(Project project);
}
