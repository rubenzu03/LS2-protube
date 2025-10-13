package com.tecnocampus.LS2.protube_back.persistence;

import com.tecnocampus.LS2.protube_back.domain.Category;
import com.tecnocampus.LS2.protube_back.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long id);
    void deleteById(Long id);

    Category save(Category category);


}
