package ctts.repository;

import ctts.entity.Project;
import ctts.entity.ProjectStatus;
import ctts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByClient(User client);

    List<Project> findByDevelopersContaining(User developer);

    long countByStatus(ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Project> searchProjects(@Param("search") String search);

    // 🔥 Methods to filter by the Project Manager
    long countByCreatedBy(User pm);

    long countByStatusAndCreatedBy(ProjectStatus status, User pm);

    List<Project> findByCreatedBy(User pm);

    @Query("SELECT p FROM Project p WHERE p.createdBy = :pm AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Project> searchProjectsByProjectManager(@Param("pm") User pm, @Param("search") String search);
}