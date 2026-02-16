package ctts.repository;

import ctts.entity.Project;
import ctts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByClient(User client);

    List<Project> findByDevelopersContaining(User developer);
}
