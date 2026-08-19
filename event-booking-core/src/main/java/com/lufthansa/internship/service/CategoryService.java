package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.CategoryRequestDTO;
import com.lufthansa.internship.dto.response.CategoryResponseDTO;
import com.lufthansa.internship.entity.Category;
import com.lufthansa.internship.entity.CategoryStatus;
import com.lufthansa.internship.exception.CategoryNotFoundException;
import com.lufthansa.internship.exception.DuplicateCategoryException;
import com.lufthansa.internship.exception.IllegalCategoryException;
import com.lufthansa.internship.mapper.CategoryMapper;
import com.lufthansa.internship.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request){

        Category c = categoryRepository.findByName(request.getName());
        if (c != null) throw new DuplicateCategoryException("Category already exists");
        Category category = categoryMapper.toEntity(request);
        category.setStatus(CategoryStatus.ACTIVE);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDTO(saved);

    }

    @Transactional
    public CategoryResponseDTO updateCategory(Integer categoryId, CategoryRequestDTO request){
        Category existing = categoryRepository.findById(categoryId).orElseThrow(()-> new CategoryNotFoundException("Category does not exist"));

        Category dupe = categoryRepository.findByName(request.getName());
        if (dupe != null) throw new DuplicateCategoryException("Category with this name already exists");

        categoryMapper.updateEntityFromDTO(request, existing);

        Category saved = categoryRepository.save(existing);
        return categoryMapper.toDTO(saved);
    }

    @Transactional
    public CategoryResponseDTO removeCategory (Integer categoryId){
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new CategoryNotFoundException("Category does not exist"));
        category.setStatus(CategoryStatus.INACTIVE);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDTO(saved);
    }



}
