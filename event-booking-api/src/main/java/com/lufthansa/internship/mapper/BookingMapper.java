package com.lufthansa.internship.mapper;


import com.lufthansa.internship.dto.request.BookingRequestDTO;
import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "eventId", target = "event.id")
    Booking toEntity(BookingRequestDTO dto);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "eventId", target = "event.id")
    BookingResponseDTO toDTO(Booking booking);


}
