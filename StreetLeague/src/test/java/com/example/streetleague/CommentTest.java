package com.example.streetleague;

import com.example.streetleague.Controller.CommentController;
import com.example.streetleague.Entity.Comment;
import com.example.streetleague.ServiceInterface.CommentService;
import com.example.streetleague.dto.commentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Nice post!");
        return comment;
    }

    private commentDTO createDTO() {
        commentDTO dto = new commentDTO();
        dto.setContent("Nice post!");
        return dto;
    }

    @Test
    void shouldAddComment() {
        commentDTO dto = createDTO();
        Comment comment = createComment();

        when(commentService.addComment(dto)).thenReturn(comment);

        ResponseEntity<Comment> response = commentController.addComment(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Nice post!", response.getBody().getContent());
        verify(commentService).addComment(dto);
    }

    @Test
    void shouldDeleteComment() {
        doNothing().when(commentService).deleteComment(1L);

        ResponseEntity<Void> response = commentController.deleteComment(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(commentService).deleteComment(1L);
    }

    @Test
    void shouldUpdateComment() {
        commentDTO dto = createDTO();
        Comment updated = createComment();

        when(commentService.updateComment(1L, dto)).thenReturn(updated);

        ResponseEntity<Comment> response = commentController.updateComment(1L, dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updated, response.getBody());
        verify(commentService).updateComment(1L, dto);
    }

    @Test
    void shouldReturnAllComments() {
        List<Comment> list = List.of(createComment());

        when(commentService.getAllComments()).thenReturn(list);

        ResponseEntity<List<Comment>> response = commentController.getAllComments();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(commentService).getAllComments();
    }

    @Test
    void shouldReturnCommentById() {
        Comment comment = createComment();

        when(commentService.getCommentById(1L)).thenReturn(comment);

        ResponseEntity<Comment> response = commentController.getCommentById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(comment, response.getBody());
        verify(commentService).getCommentById(1L);
    }

    @Test
    void shouldReturnCommentsByPost() {
        List<Comment> list = List.of(createComment());

        when(commentService.getCommentsByPost(10L)).thenReturn(list);

        ResponseEntity<List<Comment>> response = commentController.getCommentsByPost(10L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(commentService).getCommentsByPost(10L);
    }
}