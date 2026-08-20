package com.lufthansa.internship.dto.response;

import com.lufthansa.internship.entity.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {

    private Integer id;
    @NotBlank
    private String name;
    @NotNull
    private CategoryStatus status;
    @NotNull
    private List<String> events;

}
