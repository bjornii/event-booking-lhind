package com.lufthansa.internship.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestDTO {
    @NotBlank @Size(min=3)
    private String title;
    @NotBlank @Size(min=6)
    private String description;
    @NotNull
    private LocalDateTime startDateTime;
    @NotNull
    private LocalDateTime endDateTime;
    @NotNull @PositiveOrZero
    private Double price;
    @NotNull @Positive
    private Integer totalSeats;
    @NotNull @PositiveOrZero
    private Integer availableSeats;
    @NotNull @Positive
    private Integer venueId;
    @NotNull @Size(min=1)
    private List<Integer> categoryIds;

}
