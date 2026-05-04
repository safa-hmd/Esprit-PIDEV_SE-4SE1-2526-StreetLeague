package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import java.util.Objects;

public class commentDTO {

    private Long id;
    @NotBlank(message = "Content is required")
    @Size(min = 2, max = 500, message = "Content must be between 2 and 500 characters")
    private String content;

    private boolean reported;
    @NotNull(message = "Post ID is required")
    private Long postId;
    private Long userId;

    public commentDTO() {
    }

    public commentDTO(Long id, String content, boolean reported, Long postId, Long userId) {
        this.id = id;
        this.content = content;
        this.reported = reported;
        this.postId = postId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        commentDTO that = (commentDTO) o;
        return reported == that.reported && Objects.equals(id, that.id) && Objects.equals(content, that.content) && Objects.equals(postId, that.postId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, reported, postId, userId);
    }

    @Override
    public String toString() {
        return "commentDTO{" +
                "id=" + id +
                ", reported=" + reported +
                ", postId=" + postId +
                ", userId=" + userId +
                '}';
    }

    public static commentDTOBuilder builder() {
        return new commentDTOBuilder();
    }

    public static class commentDTOBuilder {
        private Long id;
        private String content;
        private boolean reported;
        private Long postId;
        private Long userId;

        public commentDTOBuilder id(Long id) { this.id = id; return this; }
        public commentDTOBuilder content(String content) { this.content = content; return this; }
        public commentDTOBuilder reported(boolean reported) { this.reported = reported; return this; }
        public commentDTOBuilder postId(Long postId) { this.postId = postId; return this; }
        public commentDTOBuilder userId(Long userId) { this.userId = userId; return this; }

        public commentDTO build() {
            return new commentDTO(id, content, reported, postId, userId);
        }
    }
}
