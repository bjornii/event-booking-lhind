package com.lufthansa.internship.mapper;

import com.lufthansa.internship.dto.request.VenueRequestDTO;
import com.lufthansa.internship.dto.response.EventResponseDTO;
import com.lufthansa.internship.dto.response.VenueResponseDTO;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.Venue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    @Mapping(target = "events", ignore = true)
    Venue toEntity(VenueRequestDTO dto);

    @Mapping(target = "events", expression = "java(mapEventTitles(venue.getEvents()))")
    VenueResponseDTO toDTO(Venue venue);

    default List<String> mapEventTitles(List<Event> events){
        if (events == null) return List.of();
        return events.stream().map(Event::getTitle).toList();
    }

}
