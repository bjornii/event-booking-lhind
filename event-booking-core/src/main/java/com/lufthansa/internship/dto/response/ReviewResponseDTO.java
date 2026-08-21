package com.lufthansa.internship.dto.response;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {

    @Positive
    private Integer id;
    @NotNull @Min(1) @Max(5)
    private Integer rating;
    @NotNull @Size(max = 300)
    private String comment;
    @NotBlank @Size(min = 4, max = 30)
    private String userName;
    @NotBlank @Size(min = 4, max = 30)
    private String eventName;

}
