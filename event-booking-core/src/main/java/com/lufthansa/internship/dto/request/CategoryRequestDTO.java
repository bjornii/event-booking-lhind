package com.lufthansa.internship.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDTO {

    @NotBlank @Size(min=3, max = 30)
    private String name;

}
