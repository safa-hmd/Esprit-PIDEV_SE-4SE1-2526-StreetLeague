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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceIMPL implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    // ← CACHE: moch final basch Lombok ma yich trou
    private final Map<String, Page<Post>> cache = new HashMap<>();

    private static final int  TITLE_MIN       = 3;
    private static final int  TITLE_MAX       = 100;
    private static final int  DESC_MIN        = 5;
    private static final int  DESC_MAX        = 500;
    private static final long IMAGE_MAX_BYTES = 5 * 1024 * 1024;

    // ===== VALIDATIONS =====

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

    // ===== METHODS =====

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
                .publishDate(LocalDate.now())
                .user(user)
                .likes(0)
                .build();

        cache.clear(); // ← faragh cache ki tzid post jdid
        return postRepository.save(post);
    }

    // ← BADDALNA: getPosts b pagination + cache
    @Override
    public Page<Post> getPosts(int page, int size) {
        String key = page + "-" + size;

        if (cache.containsKey(key)) {
            System.out.println("✅ FROM CACHE: " + key);
            return cache.get(key);
        }

        System.out.println("🔍 FROM DB: " + key);
        Page<Post> result = postRepository.findAll(PageRequest.of(page, size));
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

        cache.clear(); // ← faragh cache ki tbaddel post
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        try {
            cloudinaryService.deleteImage(post.getImageUrl());
        } catch (IOException e) {
            System.err.println("Cloudinary delete failed: " + e.getMessage());
        }

        cache.clear(); // ← faragh cache ki tamsah post
        postRepository.delete(post);
    }

    @Override
    public Post likePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikes(post.getLikes() + 1);
        return postRepository.save(post);
    }

    @Override
    public Post dislikePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (post.getLikes() > 0)
            post.setLikes(post.getLikes() - 1);
        return postRepository.save(post);
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
            sorting = Sort.by("publishDate").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sorting);

        // ki keyword wella category faragh → n7othom null
        String kw  = (keyword  == null || keyword.trim().isEmpty())
                ? null : keyword.trim();
        String cat = (category == null || category.trim().isEmpty())
                ? null : category.trim();

        // ila el ethnin null → nraja3 kol el posts (moch search)
        if (kw == null && cat == null) {
            return postRepository.findAll(pageable);
        }

        return postRepository.searchPosts(kw, cat, pageable);
    }
    public Post addPostWithUrl(postDTO dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setCategory(dto.getCategory());
        post.setImageUrl(dto.getImageUrl());
        post.setPublishDate(LocalDate.now());
        return postRepository.save(post);
    }
}