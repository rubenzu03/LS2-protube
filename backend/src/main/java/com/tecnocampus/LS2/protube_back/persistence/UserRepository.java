package com.tecnocampus.LS2.protube_back.persistence;

import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Override
    List<User> findAll();

    @Override
    Optional<User> findById(Long id);

    @Override
    void deleteById(Long id);

    @Query("UPDATE users u SET u.id = :newId WHERE u.id = u.id")
    void updateId(Long id, Long newId);

    User save(User user);
}
