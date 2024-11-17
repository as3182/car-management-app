package com.spyneai.carmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CarDTO {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @Size(max = 10, message = "You can upload up to 10 images")
    private List<String> images;

    private List<String> tags;


}
