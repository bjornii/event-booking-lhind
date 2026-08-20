package com.lufthansa.internship.mapper;


import com.lufthansa.internship.dto.request.BookingRequestDTO;
import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Booking toEntity(BookingRequestDTO dto);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "event.title", target = "eventName")
    BookingResponseDTO toDTO(Booking booking);


}
