package com.lufthansa.internship.mapper;

import com.lufthansa.internship.dto.request.EventRequestDTO;
import com.lufthansa.internship.dto.response.EventResponseDTO;
import com.lufthansa.internship.entity.Category;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(source = "venueId", target = "venue.id")
    @Mapping(source = "organizerId", target = "organizer.id")
    @Mapping(target = "categories", ignore = true)
    Event toEntity(EventRequestDTO dto);

    @Mapping(source = "venue.name", target = "venueName")
    @Mapping(source = "organizer.username", target = "organizerName")
    @Mapping(target = "categories", expression = "java(mapCategoryNames(event.getCategories()))")
    @Mapping(target = "avgRating", expression = "java(calculateAvgRating(event.getReviews()))")
    EventResponseDTO toDTO(Event event);

    default List<String> mapCategoryNames(List<Category> categories) {
        if (categories == null) return List.of();
        return categories.stream().map(Category::getName).toList();
    }

    default Double calculateAvgRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) return null;
        return reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }
}
