package ctts.repository;

import ctts.entity.User;
import ctts.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // 🔥 Get users by role (for admin dashboard)
    List<User> findByRole(Role role);

    // 🔥 Count users by role (for admin statistics)
    long countByRole(Role role);
}
