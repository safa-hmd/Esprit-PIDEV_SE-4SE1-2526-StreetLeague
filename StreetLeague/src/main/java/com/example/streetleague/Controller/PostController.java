package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Post;
import com.example.streetleague.Repository.PostRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceImp.AIImageService;
import com.example.streetleague.ServiceInterface.PostService;
import com.example.streetleague.dto.postDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final AIImageService aiImageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> addPost(
            @RequestParam("title")       String title,
            @RequestParam("description") String description,
            @RequestParam("category")    String category,
            @RequestParam("image")       MultipartFile image
    ) throws IOException {
        postDTO dto = new postDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setCategory(category);
        Post saved = postService.addPost(dto, image);

        messagingTemplate.convertAndSend("/topic/posts", Map.of(
                "type", "NEW_POST",
                "post", saved
        ));

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/getAll")
    public Page<postDTO> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {


        return postService.getPosts(page, size)
                .map(post -> {
                    postDTO dto = new postDTO(
                            post.getId(),
                            post.getTitle(),
                            post.getDescription(),
                            post.getCategory(),
                            post.getUser() != null ? post.getUser().getIdUser() : null,
                            post.getImageUrl(),
                            post.getUser() != null ? post.getUser().getFullName() : "Admin",
                            (long)(post.getComments() != null ? post.getComments().size() : 0),
                            (long) post.getLikes()
                    );

                    boolean liked = false;

                    if (userId != null) {
                        liked = postRepository.isLiked(post.getId(), userId);
                    }

                    dto.setLiked(liked);
//                    System.out.println("POST ID: " + post.getId());
//                    System.out.println("USER ID: " + userId);
//                    System.out.println("LIKED BY USERS: " + liked);

                    dto.setCreatedAt(post.getCreatedAt() != null ? post.getCreatedAt().toString() : null);
                    dto.setUpdatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null);
                    return dto;
                });
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<postDTO> updatePost(@PathVariable Long id, @RequestBody postDTO dto) {
        Post updated = postService.updatePost(id, dto);

        messagingTemplate.convertAndSend("/topic/posts", Map.of(
                "type", "UPDATE_POST",
                "postId", updated.getId(),
                "title", updated.getTitle(),
                "description", updated.getDescription(),
                "category", updated.getCategory()
        ));

        // ✅ Retourne DTO au lieu de l'entity Post
        postDTO response = new postDTO();
        response.setId(updated.getId());
        response.setTitle(updated.getTitle());
        response.setDescription(updated.getDescription());
        response.setCategory(updated.getCategory());
        response.setImageUrl(updated.getImageUrl());

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);

        messagingTemplate.convertAndSend("/topic/posts", Map.of(
                "type", "DELETE_POST",
                "postId", id
        ));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<Map<String, Object>> likePost(
            @PathVariable Long id
    ) {
        // الـ user يجي من JWT مباشرة في الـ service
        Post post = postService.likePost(id);

        // نجيب الـ userId من SecurityContext هنا أيضاً
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email)
                .map(u -> u.getIdUser())
                .orElse(null);

        boolean liked = userId != null && post.getLikedByUsers().contains(userId);

        messagingTemplate.convertAndSend("/topic/posts", Map.of(
                "type", "LIKE_UPDATE",
                "postId", id,
                "likes", post.getLikes(),
                "liked", liked
        ));

        return ResponseEntity.ok(Map.of("likes", post.getLikes(), "liked", liked));
    }

    @PostMapping("/dislike/{id}")
    public ResponseEntity<Map<String, Object>> dislikePost(
            @PathVariable Long id
    ) {
        Post post = postService.dislikePost(id);

        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email)
                .map(u -> u.getIdUser())
                .orElse(null);

        boolean liked = userId != null && post.getLikedByUsers().contains(userId);

        messagingTemplate.convertAndSend("/topic/posts", Map.of(
                "type", "LIKE_UPDATE",
                "postId", id,
                "likes", post.getLikes(),
                "liked", liked
        ));

        return ResponseEntity.ok(Map.of("likes", post.getLikes(), "liked", liked));
    }

    @GetMapping("/stats/top")
    public ResponseEntity<Map<String, Object>> getTopPosts() {
        Map<String, Object> result = new HashMap<>();

        postService.getMostCommentedPost().ifPresentOrElse(
                p -> result.put("mostCommented", p),
                () -> result.put("mostCommented", null)
        );

        postService.getMostLikedPost().ifPresentOrElse(
                p -> result.put("mostLiked", p),
                () -> result.put("mostLiked", null)
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public Page<postDTO> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        String kw  = (keyword  != null && !keyword.trim().isEmpty())  ? keyword.trim()  : null;
        String cat = (category != null && !category.trim().isEmpty()) ? category.trim() : null;

        return postService.searchPosts(kw, cat, sort, page, size)
                .map(post -> {
                    postDTO dto = new postDTO(
                            post.getId(),
                            post.getTitle(),
                            post.getDescription(),
                            post.getCategory(),
                            post.getUser() != null ? post.getUser().getIdUser() : null,
                            post.getImageUrl(),
                            post.getUser() != null ? post.getUser().getFullName() : "Admin",
                            (long)(post.getComments() != null ? post.getComments().size() : 0),
                            (long) post.getLikes()
                    );
                    dto.setLiked(userId != null && post.getLikedByUsers().contains(userId));
                    dto.setCreatedAt(post.getCreatedAt() != null ? post.getCreatedAt().toString() : null);
                    dto.setUpdatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null);
                    return dto;
                });
    }

    @GetMapping("/stats/general")
    public ResponseEntity<Map<String, Object>> getGeneralStats() {
        List<Post> allPosts = postRepository.findAll();
        long totalPosts    = allPosts.size();
        long totalLikes    = allPosts.stream().mapToLong(Post::getLikes).sum();
        long totalComments = allPosts.stream()
                .mapToLong(p -> p.getComments() != null ? p.getComments().size() : 0).sum();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPosts",    totalPosts);
        stats.put("totalLikes",    totalLikes);
        stats.put("totalComments", totalComments);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/generate-image")
    public ResponseEntity<?> generateImage(@RequestParam String prompt) {
        try {
            String imageUrl = aiImageService.generateAndUpload(prompt);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            log.error("AI image generation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/add-with-url", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> addPostWithUrl(
            @RequestParam("title")       String title,
            @RequestParam("description") String description,
            @RequestParam("category")    String category,
            @RequestParam("imageUrl")    String imageUrl
    ) {
        postDTO dto = new postDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setCategory(category);
        dto.setImageUrl(imageUrl);
        Post saved = postService.addPostWithUrl(dto);

        messagingTemplate.convertAndSend("/topic/posts", Map.of(
                "type", "NEW_POST",
                "post", saved
        ));

        return ResponseEntity.ok(saved);
    }
}