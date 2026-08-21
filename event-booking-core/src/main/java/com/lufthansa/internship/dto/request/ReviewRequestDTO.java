package com.lufthansa.internship.dto.request;

import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
