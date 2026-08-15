package com.lufthansa.internship.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate startDateTime;
    @NotNull
    private LocalDate endDateTime;
    @NotNull
    private Double price;
    @NotNull
    private Integer totalSeats;
    @NotNull
    private Integer availableSeats;
    @NotNull
    private Integer venueId;
    @NotNull
    private Integer organizerId;
    @NotNull
    private List<Integer> categoryIds;

}
