package com.lufthansa.internship.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {

    @NotNull
    private Integer rating;
    @NotBlank
    private String comment;
    @NotBlank
    private String userName;
    @NotBlank
    private String eventName;

}
