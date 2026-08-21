package com.lufthansa.internship.dto.response;

import com.lufthansa.internship.entity.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {

    @Positive
    private Integer id;
    @NotBlank @Size(min = 4, max = 30)
    private String name;
    @NotNull
    private CategoryStatus status;
    @NotNull
    private List<String> events;

}
