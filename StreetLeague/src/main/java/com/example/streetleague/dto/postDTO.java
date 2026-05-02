package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class postDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotBlank(message = "Categorie is required")
    private String category;

    private Long userId;
    private String imageUrl;
    private String adminName;
    private Long commentCount;
    private Integer likes;
    private String createdAt;
    private String updatedAt;
    private boolean liked; // ← NOUVEAU

    public postDTO(Long id, String title, String description, String category,
                   Long userId, String imageUrl, String adminName,
                   Long commentCount, Long likes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.adminName = adminName;
        this.commentCount = commentCount;
        this.likes = likes != null ? likes.intValue() : 0;
        this.liked = false;
    }
}