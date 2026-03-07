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

    // 🔥 Get users by role (for admin dashboard)
    List<User> findByRole(Role role);

    // 🔥 Count users by role (for admin statistics)
    long countByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchUsersByRole(@Param("role") Role role, @Param("search") String search);



    // 🔥 New methods to filter by the Admin
    long countByRoleAndCreatedByAdmin(Role role, User admin);

    List<User> findByRoleAndCreatedByAdmin(Role role, User admin);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.createdByAdmin = :admin AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchUsersByRoleAndAdmin(@Param("role") Role role, @Param("admin") User admin, @Param("search") String search);


}
