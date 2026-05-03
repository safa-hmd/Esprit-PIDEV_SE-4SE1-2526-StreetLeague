package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.Comment;
import com.example.streetleague.dto.commentDTO;

import java.util.List;

public interface CommentService {
    Comment addComment(commentDTO dto);
    void deleteComment(Long id);
    Comment updateComment(Long id, commentDTO dto);    List<Comment> getAllComments();
    Comment getCommentById(Long id);
    List<Comment> getCommentsByPost(Long postId);
}
