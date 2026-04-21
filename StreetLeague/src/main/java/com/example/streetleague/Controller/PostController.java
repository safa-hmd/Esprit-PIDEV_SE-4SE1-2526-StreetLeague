package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Post;
import com.example.streetleague.ServiceInterface.PostService;
import com.example.streetleague.dto.postDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    // ✅ ADD POST WITH IMAGE
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> addPost(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        postDTO dto = new postDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setImageData(image.getBytes()); // Convert to bytes
        dto.setImageType(image.getContentType()); // Store MIME type

        return ResponseEntity.ok(postService.addPost(dto));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(postService.getPosts());
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
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Post post = postService.getPost(id);
        if (post != null && post.getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(post.getImageType()))
                    .body(post.getImageData());
        }
        return ResponseEntity.notFound().build();
    }
}