package com.lufthansa.internship.mapper;

import com.lufthansa.internship.dto.request.CategoryRequestDTO;
import com.lufthansa.internship.dto.response.CategoryResponseDTO;
import com.lufthansa.internship.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequestDTO dto);
    CategoryResponseDTO toDTO(Category category);

}
