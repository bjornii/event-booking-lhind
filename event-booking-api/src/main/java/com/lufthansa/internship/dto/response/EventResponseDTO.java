package com.lufthansa.internship.dto.response;

import com.lufthansa.internship.entity.*;
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
public class EventResponseDTO {
    @NotNull
    private Integer id;
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
    @NotBlank
    private String venueName;
    @NotBlank
    private String organizerName;
    @NotNull
    private List<String> categories;

    private Double avgRating;

}
