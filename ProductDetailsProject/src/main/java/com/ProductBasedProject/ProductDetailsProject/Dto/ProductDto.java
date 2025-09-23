package com.ProductBasedProject.ProductDetailsProject.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductDto {
    @NotEmpty(message = "the name is required")
    private String name;
    @NotEmpty(message = "Brand is required")
    private String brand;
    @NotEmpty(message = "Category is required")
    private String category;
    @Min(0)
    private double price;
    @Size(min = 10,message = "The description should be atleast 10 characters")
    @Size(max = 2000,message = "The description cannot exceed 2000 characters")
    private String description;
    private MultipartFile imageFile;
}
