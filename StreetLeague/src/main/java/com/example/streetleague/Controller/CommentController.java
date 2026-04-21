package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Comment;
import com.example.streetleague.ServiceInterface.CommentService;
import com.example.streetleague.dto.commentDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")

public class CommentController{
 private final CommentService commentService;

 @PostMapping("/add")
 @PreAuthorize("hasRole('PLAYER')")
 public ResponseEntity<Comment> addComment(@Valid @RequestBody commentDTO dto) {
  return ResponseEntity.ok(commentService.addComment(dto));
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
  return ResponseEntity.ok(commentService.updateComment(id, dto));
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
