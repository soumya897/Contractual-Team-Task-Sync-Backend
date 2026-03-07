package ctts.repository;

import ctts.entity.User;
import ctts.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // 🔥 Get users by role
    List<User> findByRole(Role role);

    // 🔥 Count users by role
    long countByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchUsersByRole(@Param("role") Role role, @Param("search") String search);

    // 🔥 Updated methods to filter by the Project Manager
    long countByRoleAndCreatedByProjectManager(Role role, User pm);

    List<User> findByRoleAndCreatedByProjectManager(Role role, User pm);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.createdByProjectManager = :pm AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchUsersByRoleAndProjectManager(@Param("role") Role role, @Param("pm") User pm, @Param("search") String search);
}