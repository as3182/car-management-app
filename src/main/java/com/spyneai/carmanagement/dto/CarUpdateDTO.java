package com.spyneai.carmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CarUpdateDTO {

    private Long id;

    private String title;

    private String description;

    @Size(max = 10, message = "You can upload up to 10 images")
    private List<String> images; // Base64-encoded images

    private List<String> tags;


}
