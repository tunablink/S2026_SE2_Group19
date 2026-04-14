package com.example.cybersec.user.repository;

import com.example.cybersec.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA Repository dành cho Entity User.
 * <p>
 * Cung cấp các thao tác CRUD cơ bản cho bảng users trong cơ sở dữ liệu,
 * đi kèm query tùy chỉnh tìm user theo username.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
