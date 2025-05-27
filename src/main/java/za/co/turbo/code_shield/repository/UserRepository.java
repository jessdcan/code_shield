package za.co.turbo.code_shield.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.turbo.code_shield.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
