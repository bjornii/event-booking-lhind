package com.lufthansa.internship.repository;

import com.lufthansa.internship.entity.Category;
import com.lufthansa.internship.entity.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);


    @Query("SELECT c.status FROM Category c WHERE c.id = :categoryId")
    CategoryStatus findStatusById(@Param("categoryId") Integer categoryId);


    List<Category> findAllByNameIn(List<String> categories);

    Category findByNameIgnoreCase(@NotBlank String name);

    Optional<Category> findByIdAndStatus(Integer categoryId, CategoryStatus categoryStatus);
}
