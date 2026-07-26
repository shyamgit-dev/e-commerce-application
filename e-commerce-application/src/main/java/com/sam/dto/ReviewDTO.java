package com.sam.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    private String username;

    @Size(max=1000)
    private String comments;

    @Min(1) @Max(5)
    private Double rating;

    private LocalDateTime createdAt;

}
