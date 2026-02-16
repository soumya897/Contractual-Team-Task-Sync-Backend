package ctts.repository;

import ctts.entity.User;
import ctts.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // 🔥 Count users by role (for admin profile dashboard)
    long countByRole(Role role);
}
