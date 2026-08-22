package com.lufthansa.internship.mapper;

import com.lufthansa.internship.dto.request.ReviewRequestDTO;
import com.lufthansa.internship.dto.response.ReviewResponseDTO;
import com.lufthansa.internship.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "eventId", target = "event.id")
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewRequestDTO dto);

    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "event.title", target = "eventName")
    ReviewResponseDTO toDTO(Review review);



}
