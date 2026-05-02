package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class commentDTO {

    private Long id;
    @NotBlank(message = "Content is required")
    @Size(min = 2, max = 500, message = "Content must be between 2 and 500 characters")
    private String content;

    private boolean reported;
    @NotNull(message = "Post ID is required")
    private Long postId;
    private Long userId;
}
