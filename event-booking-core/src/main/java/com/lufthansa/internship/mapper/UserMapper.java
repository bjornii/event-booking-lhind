package com.lufthansa.internship.mapper;

import com.lufthansa.internship.dto.request.UserRegisterRequestDTO;
import com.lufthansa.internship.dto.response.UserResponseDTO;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookingMapper.class, ReviewMapper.class})
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogIn", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(UserRegisterRequestDTO dto);

    @Mapping(source = "role", target = "role")
    @Mapping(target = "events", expression = "java(mapEventTitles(user.getEvents()))")
    UserResponseDTO toDTO(User user);

    default List<String> mapEventTitles(List<Event> events){
        if (events == null) return List.of();
        return events.stream().map(Event::getTitle).toList();
    }


}
