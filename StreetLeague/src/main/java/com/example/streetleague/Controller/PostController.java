package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Post;
import com.example.streetleague.Repository.PostRepository;
import com.example.streetleague.ServiceImp.AIImageService;
import com.example.streetleague.ServiceInterface.PostService;
import com.example.streetleague.dto.postDTO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final AIImageService aiImageService;

    // ✅ @AllArgsConstructor تولّد الconstructor تلقائياً — ما تكتبش أي constructor يدوي!

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
        return ResponseEntity.ok(postService.addPost(dto, image));
    }

    @GetMapping("/getAll")
    public Page<postDTO> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return postService.getPosts(page, size)
                .map(post -> new postDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getDescription(),
                        post.getCategory(),
                        post.getUser() != null ? post.getUser().getId() : null,
                        post.getImageUrl(),
                        post.getUser() != null ? post.getUser().getFullName() : "Admin",
                        (long) (post.getComments() != null ? post.getComments().size() : 0),
                        (long) post.getLikes()
                ));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody postDTO dto) {
        return ResponseEntity.ok(postService.updatePost(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/like/{id}")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<Post> likePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.likePost(id));
    }

    @PostMapping("/dislike/{id}")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<Post> dislikePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.dislikePost(id));
    }

    @GetMapping("/stats/top")
    public ResponseEntity<Map<String, Object>> getTopPosts() {
        Map<String, Object> result = new HashMap<>();
        postService.getMostCommentedPost()
                .ifPresent(p -> result.put("mostCommented", p));
        postService.getMostLikedPost()
                .ifPresent(p -> result.put("mostLiked", p));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public Page<postDTO> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        String kw  = (keyword  != null && !keyword.trim().isEmpty())  ? keyword.trim()  : null;
        String cat = (category != null && !category.trim().isEmpty()) ? category.trim() : null;

        return postService.searchPosts(kw, cat, sort, page, size)
                .map(post -> new postDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getDescription(),
                        post.getCategory(),
                        post.getUser() != null ? post.getUser().getId() : null,
                        post.getImageUrl(),
                        post.getUser() != null ? post.getUser().getFullName() : "Admin",
                        (long) (post.getComments() != null ? post.getComments().size() : 0),
                        (long) post.getLikes()
                ));
    }

    @GetMapping("/stats/general")
    public ResponseEntity<Map<String, Object>> getGeneralStats() {
        List<Post> allPosts = postRepository.findAll();

        long totalPosts    = allPosts.size();
        long totalLikes    = allPosts.stream().mapToLong(Post::getLikes).sum();
        long totalComments = allPosts.stream()
                .mapToLong(p -> p.getComments() != null ? p.getComments().size() : 0)
                .sum();

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
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
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
        return ResponseEntity.ok(postService.addPostWithUrl(dto));
    }
}