package com.lufthansa.internship.dto.request;

import com.lufthansa.internship.entity.Event;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VenueRequestDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotNull
    private Integer capacity;

}
