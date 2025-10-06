package com.tecnocampus.LS2.protube_back.persistence;

import com.tecnocampus.LS2.protube_back.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    @Override
    Optional<Video> findById(Long id);

    @Override
    List<Video> findAll();

    Video save(Video video);

    void deleteById(Long id);

    @Query("UPDATE videos v SET v.id = :newId WHERE v.id = :id")
    void updateId(Long id, Long newId);
}
