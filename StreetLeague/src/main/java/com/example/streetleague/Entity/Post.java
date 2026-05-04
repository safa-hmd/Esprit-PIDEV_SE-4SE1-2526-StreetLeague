package com.example.streetleague.Entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import com.example.streetleague.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private int likes = 0;

    private String imageUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Comment> comments;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ElementCollection
    @CollectionTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "user_id")
    private Set<Long> likedByUsers = new HashSet<>();

    public Post() {
    }

    public Post(Long id, String title, String description, String category, LocalDateTime createdAt, LocalDateTime updatedAt, int likes, String imageUrl, List<Comment> comments, User user, Set<Long> likedByUsers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likes = likes;
        this.imageUrl = imageUrl;
        this.comments = comments;
        this.user = user;
        this.likedByUsers = likedByUsers != null ? likedByUsers : new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Long> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(Set<Long> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return likes == post.likes && Objects.equals(id, post.id) && Objects.equals(title, post.title) && Objects.equals(description, post.description) && Objects.equals(category, post.category) && Objects.equals(createdAt, post.createdAt) && Objects.equals(updatedAt, post.updatedAt) && Objects.equals(imageUrl, post.imageUrl) && Objects.equals(user, post.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, category, createdAt, updatedAt, likes, imageUrl, user);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", likes=" + likes +
                ", imageUrl='" + imageUrl + '\'' +
                ", user=" + user +
                '}';
    }

    public static PostBuilder builder() {
        return new PostBuilder();
    }

    public static class PostBuilder {
        private Long id;
        private String title;
        private String description;
        private String category;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int likes;
        private String imageUrl;
        private List<Comment> comments;
        private User user;
        private Set<Long> likedByUsers = new HashSet<>();

        public PostBuilder id(Long id) { this.id = id; return this; }
        public PostBuilder title(String title) { this.title = title; return this; }
        public PostBuilder description(String description) { this.description = description; return this; }
        public PostBuilder category(String category) { this.category = category; return this; }
        public PostBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PostBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public PostBuilder likes(int likes) { this.likes = likes; return this; }
        public PostBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public PostBuilder comments(List<Comment> comments) { this.comments = comments; return this; }
        public PostBuilder user(User user) { this.user = user; return this; }
        public PostBuilder likedByUsers(Set<Long> likedByUsers) { this.likedByUsers = likedByUsers; return this; }

        public Post build() {
            return new Post(id, title, description, category, createdAt, updatedAt, likes, imageUrl, comments, user, likedByUsers);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}