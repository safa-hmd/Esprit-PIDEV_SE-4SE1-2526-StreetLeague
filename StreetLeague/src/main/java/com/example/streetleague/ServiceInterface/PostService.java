package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.Post;
import com.example.streetleague.dto.postDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PostService {

    Post addPost(postDTO dto, MultipartFile image) throws IOException;

    // ← BADDALNA: b pagination
    Page<Post> getPosts(int page, int size);

    Post getPost(Long id);

    Post updatePost(Long id, postDTO dto);

    void deletePost(Long id);

    Post likePost(Long id);

    Post dislikePost(Long postId);

    Optional<postDTO> getMostCommentedPost();

    Optional<postDTO> getMostLikedPost();

    Page<Post> searchPosts(String keyword, String category, String sort, int page, int size);
    Post addPostWithUrl(postDTO dto);
}