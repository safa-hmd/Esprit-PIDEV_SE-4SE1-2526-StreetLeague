package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Post;
import com.example.streetleague.Repository.PostRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.PostService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.postDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceIMPL implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // ===== CONSTANTES DE VALIDATION =====
    private static final int TITLE_MIN = 3;
    private static final int TITLE_MAX = 100;
    private static final int DESC_MIN = 5;
    private static final int DESC_MAX = 500;
    private static final long IMAGE_MAX_BYTES = 5 * 1024 * 1024; // 5 MB

    // ===== HELPERS =====
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (title.trim().length() < TITLE_MIN) {
            throw new IllegalArgumentException(
                    "Title must be at least " + TITLE_MIN + " characters");
        }
        if (title.trim().length() > TITLE_MAX) {
            throw new IllegalArgumentException(
                    "Title must not exceed " + TITLE_MAX + " characters");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (description.trim().length() < DESC_MIN) {
            throw new IllegalArgumentException(
                    "Description must be at least " + DESC_MIN + " characters");
        }
        if (description.trim().length() > DESC_MAX) {
            throw new IllegalArgumentException(
                    "Description must not exceed " + DESC_MAX + " characters");
        }
    }

    private void validateImage(byte[] imageData, String imageType) {
        if (imageData == null || imageData.length == 0) {
            throw new IllegalArgumentException("Image cannot be empty");
        }
        if (imageData.length > IMAGE_MAX_BYTES) {
            throw new IllegalArgumentException("Image size must not exceed 5MB");
        }
        if (imageType == null || !imageType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "File must be an image (JPEG, PNG, etc.)");
        }
    }

    // ===== MÉTHODES =====

    @Override
    public Post addPost(postDTO dto) {
        // ✅ Validation
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());
        validateImage(dto.getImageData(), dto.getImageType());

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription().trim())
                .imageData(dto.getImageData())
                .imageType(dto.getImageType())
                .publishDate(LocalDate.now())
                .user(currentUser)
                .likes(0)
                .build();

        return postRepository.save(post);
    }

    @Override
    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post getPost(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Override
    public Post updatePost(Long id, postDTO dto) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        // ✅ Validation
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());

        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        existing.setTitle(dto.getTitle().trim());
        existing.setDescription(dto.getDescription().trim());

        return postRepository.save(existing);
    }

    @Override
    public void deletePost(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }

    @Override
    public Post likePost(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        post.setLikes(post.getLikes() + 1);
        return postRepository.save(post);
    }
}