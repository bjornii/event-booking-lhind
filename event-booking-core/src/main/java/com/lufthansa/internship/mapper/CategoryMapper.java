package com.lufthansa.internship.mapper;

import com.lufthansa.internship.dto.request.CategoryRequestDTO;
import com.lufthansa.internship.dto.response.CategoryResponseDTO;
import com.lufthansa.internship.entity.Category;
import com.lufthansa.internship.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "events", ignore = true)
    Category toEntity(CategoryRequestDTO dto);

    @Mapping(target = "events", expression = "java(mapEventTitles(category.getEvents()))")
    CategoryResponseDTO toDTO(Category category);

    default List<String> mapEventTitles(List<Event> events){
        if (events == null) return List.of();
        return events.stream().map(Event::getTitle).toList();
    }

}
