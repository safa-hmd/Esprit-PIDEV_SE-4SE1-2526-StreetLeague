package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Comment;
import com.example.streetleague.ServiceInterface.CommentService;
import com.example.streetleague.dto.commentDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

 private final CommentService commentService;
 private final SimpMessagingTemplate messagingTemplate; // ✅

 @PostMapping("/add")
 @PreAuthorize("hasRole('PLAYER')")
 public ResponseEntity<Comment> addComment(@Valid @RequestBody commentDTO dto) {
  Comment saved = commentService.addComment(dto);

  // ✅ broadcast للـ post المعني
  messagingTemplate.convertAndSend("/topic/comments/" + dto.getPostId(), Map.of(
          "type", "NEW_COMMENT",
          "comment", saved
  ));

  return ResponseEntity.ok(saved);
 }

 @DeleteMapping("/delete/{id}")
 @PreAuthorize("hasRole('PLAYER')")
 public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
  commentService.deleteComment(id);
  return ResponseEntity.noContent().build();
 }

 @PutMapping("/update/{id}")
 @PreAuthorize("hasRole('PLAYER')")
 public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody commentDTO dto) {
  Comment updated = commentService.updateComment(id, dto);

  // ✅ broadcast update
  messagingTemplate.convertAndSend("/topic/comments/" + dto.getPostId(), Map.of(
          "type", "UPDATE_COMMENT",
          "comment", updated
  ));

  return ResponseEntity.ok(updated);
 }

 @GetMapping("/getAll")
 public ResponseEntity<List<Comment>> getAllComments() {
  return ResponseEntity.ok(commentService.getAllComments());
 }

 @GetMapping("/getById/{id}")
 public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
  return ResponseEntity.ok(commentService.getCommentById(id));
 }

 @GetMapping("/post/{postId}")
 public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
  return ResponseEntity.ok(commentService.getCommentsByPost(postId));
 }
}