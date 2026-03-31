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

    @Override
    public Post addPost(postDTO dto) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .imageData(dto.getImageData()) // ✅ hadha
                .imageType(dto.getImageType())   // ✅ w hadha
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
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public Post updatePost(Long id, postDTO dto) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());

        return postRepository.save(existing);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public Post likePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setLikes(post.getLikes() + 1);
        return postRepository.save(post);
    }
}