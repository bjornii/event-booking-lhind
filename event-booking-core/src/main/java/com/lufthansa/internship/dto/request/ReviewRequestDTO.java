package com.lufthansa.internship.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDTO {

    @NotNull @Min(1) @Max(5)
    private Integer rating;
    @NotNull @Size(max = 300)
    private String comment;
    @NotNull @Positive
    private Integer eventId;

    public String getComment() {
        return (comment == null || comment.trim().isEmpty()) ? "" : comment.trim();
    }

}
