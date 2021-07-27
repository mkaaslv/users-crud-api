package com.example.userscrudapi.repo;

import com.example.userscrudapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long>{
    // here go special query methods
    Optional<User> findUserById(Long id);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);

    List<User> findByDeletedFalse();
    List<User> findByDeletedTrue();
}
