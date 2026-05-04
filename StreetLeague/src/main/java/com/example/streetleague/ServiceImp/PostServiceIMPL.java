package com.example.streetleague.ServiceImp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.example.streetleague.Entity.Post;
import com.example.streetleague.Repository.PostRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.PostService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.postDTO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PostServiceIMPL implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    private final Map<String, Page<Post>> cache = new HashMap<>();

    private static final int TITLE_MIN = 3;
    private static final int TITLE_MAX = 100;
    private static final int DESC_MIN = 5;
    private static final int DESC_MAX = 500;
    private static final long IMAGE_MAX_BYTES = 5 * 1024 * 1024;

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be empty");
        if (title.length() < TITLE_MIN)
            throw new IllegalArgumentException("Title too short");
        if (title.length() > TITLE_MAX)
            throw new IllegalArgumentException("Title too long");
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty())
            throw new IllegalArgumentException("Description cannot be empty");
        if (description.length() < DESC_MIN)
            throw new IllegalArgumentException("Description too short");
        if (description.length() > DESC_MAX)
            throw new IllegalArgumentException("Description too long");
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty())
            throw new IllegalArgumentException("Category cannot be empty");
    }

    private void validateImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty())
            throw new IllegalArgumentException("Image is required");
        if (image.getSize() > IMAGE_MAX_BYTES)
            throw new IllegalArgumentException("Image too large (max 5MB)");
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new IllegalArgumentException("Invalid image type");
    }

    @Override
    public Post addPost(postDTO dto, MultipartFile image) throws IOException {
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());
        validateCategory(dto.getCategory());
        validateImage(image);

        String imageUrl = cloudinaryService.uploadImage(image);

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription().trim())
                .category(dto.getCategory().trim())
                .imageUrl(imageUrl)
                .user(user)
                .likes(0)
                .build();

        cache.clear();
        return postRepository.save(post);
    }

    @Override
    public Page<Post> getPosts(int page, int size) {
        String key = page + "-" + size;

        if (cache.containsKey(key)) {
//            log.info("FROM CACHE: " + key);
            return cache.get(key);
        }

//        log.info("🔍 FROM DB: " + key);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> result = postRepository.findAll(pageable);

        cache.put(key, result);
        return result;
    }

    @Override
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public Post updatePost(Long id, postDTO dto) {
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());
        validateCategory(dto.getCategory());

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(dto.getTitle().trim());
        post.setDescription(dto.getDescription().trim());
        post.setCategory(dto.getCategory().trim());

        cache.clear();
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        try {
            cloudinaryService.deleteImage(post.getImageUrl());
        } catch (IOException e) {
            log.error("Cloudinary delete failed: " + e.getMessage());
        }

        cache.clear();
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public Post likePost(Long postId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getLikedByUsers().contains(user.getIdUser())) {
            post.getLikedByUsers().add(user.getIdUser());
            post.setLikes(post.getLikedByUsers().size());
            post = postRepository.save(post);
        }
        return post;
    }

    @Override
    @Transactional
    public Post dislikePost(Long postId) {
        try {
            String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

//            log.info("📊 Post {} - Current likes: {}", postId, post.getLikes());

            if (post.getLikedByUsers().contains(user.getIdUser())) {
                post.getLikedByUsers().remove(user.getIdUser());
                post.setLikes(post.getLikedByUsers().size());
                cache.clear();
                Post saved = postRepository.save(post);
//                log.info("✅ Post {} disliked successfully - New likes: {}", postId, saved.getLikes());
                return saved;
            }

            return post;
        } catch (Exception e) {
            log.error("❌ Error in dislikePost: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to dislike post: " + e.getMessage());
        }
    }

    @Override
    public Optional<postDTO> getMostCommentedPost() {
        return postRepository.findMostCommentedPost();
    }

    @Override
    public Optional<postDTO> getMostLikedPost() {
        return postRepository.findMostLikedPost();
    }

    @Override
    public Page<Post> searchPosts(String keyword, String category,
                                  String sort, int page, int size) {
        Sort sorting;
        if ("likes".equalsIgnoreCase(sort)) {
            sorting = Sort.by("likes").descending();
        } else {
            sorting = Sort.by("createdAt").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sorting);

        String kw = (keyword == null || keyword.trim().isEmpty())
                ? null : keyword.trim();
        String cat = (category == null || category.trim().isEmpty())
                ? null : category.trim();

        if (kw == null && cat == null) {
            return postRepository.findAll(pageable);
        }

        return postRepository.searchPosts(kw, cat, pageable);
    }

    public Post addPostWithUrl(postDTO dto) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setCategory(dto.getCategory());
        post.setImageUrl(dto.getImageUrl());
        post.setUser(user);
        post.setLikes(0);

        cache.clear();
        return postRepository.save(post);
    }

}