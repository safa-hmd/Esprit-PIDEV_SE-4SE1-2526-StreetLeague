package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Comment;
import com.example.streetleague.Entity.Post;
import com.example.streetleague.Repository.CommentRepository;
import com.example.streetleague.Repository.PostRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.CommentService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.commentDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceIMPL implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final RateLimiterService rateLimiterService;

    @Override
    public Comment addComment(commentDTO dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + dto.getPostId()));

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ 1. Rate Limiting
        if (!rateLimiterService.tryConsume(currentUser.getIdUser())) {
            throw new RuntimeException("Too many requests, please wait a minute");
        }

        // ✅ 2. Spam Detection
        List<Comment> lastComments = commentRepository
                .findLastCommentsByUser(currentUser.getIdUser())
                .stream()
                .limit(5)
                .toList();

        long count = lastComments.stream()
                .filter(c -> c.getContent().equalsIgnoreCase(dto.getContent()))
                .count();

        if (count >= 3) {
            throw new RuntimeException("Spam detected: repeated comment");
        }

        // ✅ 3. Save
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .user(currentUser)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
    @Override
    public Comment updateComment(Long id, commentDTO dto) {
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        existing.setContent(dto.getContent());
        existing.setUpdatedAt(LocalDateTime.now());

        if (dto.getPostId() != null) {
            Post post = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            existing.setPost(post);
        }

        return commentRepository.save(existing);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }


}