package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import java.util.Objects;

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

    public postDTO() {
    }

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

    public postDTO(Long id, String title, String description, String category, Long userId, String imageUrl, String adminName, Long commentCount, Integer likes, String createdAt, String updatedAt, boolean liked) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.adminName = adminName;
        this.commentCount = commentCount;
        this.likes = likes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.liked = liked;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public Long getCommentCount() { return commentCount; }
    public void setCommentCount(Long commentCount) { this.commentCount = commentCount; }

    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        postDTO postDTO = (postDTO) o;
        return liked == postDTO.liked && Objects.equals(id, postDTO.id) && Objects.equals(title, postDTO.title) && Objects.equals(userId, postDTO.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, userId, liked);
    }

    @Override
    public String toString() {
        return "postDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", userId=" + userId +
                ", liked=" + liked +
                '}';
    }

    public static postDTOBuilder builder() {
        return new postDTOBuilder();
    }

    public static class postDTOBuilder {
        private Long id;
        private String title;
        private String description;
        private String category;
        private Long userId;
        private String imageUrl;
        private String adminName;
        private Long commentCount;
        private Integer likes;
        private String createdAt;
        private String updatedAt;
        private boolean liked;

        public postDTOBuilder id(Long id) { this.id = id; return this; }
        public postDTOBuilder title(String title) { this.title = title; return this; }
        public postDTOBuilder description(String description) { this.description = description; return this; }
        public postDTOBuilder category(String category) { this.category = category; return this; }
        public postDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public postDTOBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public postDTOBuilder adminName(String adminName) { this.adminName = adminName; return this; }
        public postDTOBuilder commentCount(Long commentCount) { this.commentCount = commentCount; return this; }
        public postDTOBuilder likes(Integer likes) { this.likes = likes; return this; }
        public postDTOBuilder createdAt(String createdAt) { this.createdAt = createdAt; return this; }
        public postDTOBuilder updatedAt(String updatedAt) { this.updatedAt = updatedAt; return this; }
        public postDTOBuilder liked(boolean liked) { this.liked = liked; return this; }

        public postDTO build() {
            return new postDTO(id, title, description, category, userId, imageUrl, adminName, commentCount, likes, createdAt, updatedAt, liked);
        }
    }
}