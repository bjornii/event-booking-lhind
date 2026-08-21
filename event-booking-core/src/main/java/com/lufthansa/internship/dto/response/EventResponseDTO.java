package com.lufthansa.internship.dto.response;

import com.lufthansa.internship.entity.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDTO {
    @NotNull @Positive
    private Integer id;
    @NotBlank @Size(min = 4, max = 30)
    private String title;
    @NotBlank @Size(min = 4, max = 30)
    private String description;
    @NotNull
    private LocalDate startDateTime;
    @NotNull
    private LocalDate endDateTime;
    @NotNull @PositiveOrZero
    private Double price;
    @NotBlank @Size(min = 4, max = 30)
    private String venueName;
    @NotBlank @Size(min = 4, max = 30)
    private String organizerName;
    @NotNull @Min(1)
    private List<String> categories;
    @Min(1) @Max(5)
    private Double avgRating;

}
